package com.example.echo.global.util

import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.UploadCustomException
import jakarta.annotation.PostConstruct
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class UploadUtil {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${com.example.upload.path}")
    private lateinit var uploadPath: String

    @PostConstruct
    fun init() {
        val tempDir = File(uploadPath)

        // 업로드 디렉토리가 없으면 생성
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw UploadCustomException(ErrorCode.FILE_UPLOAD_FAILED)
        }

        // 업로드 경로가 디렉토리인지 확인
        if (!tempDir.isDirectory) {
            throw UploadCustomException(ErrorCode.INVALID_UPLOAD_PATH)
        }
        uploadPath = tempDir.absolutePath
        logger.info("--- uploadPath : $uploadPath")
    }

    fun upload(file: MultipartFile): String {

        validateImageFile(file)

        val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
        val destinationFile = File(uploadPath, fileName)

        try {
            file.transferTo(destinationFile)
        } catch (e: IOException) {
            logger.error("파일 업로드 실패: ${e.message}")
            throw UploadCustomException(ErrorCode.FILE_UPLOAD_FAILED)
        }

        return destinationFile.path
    }

    fun upload(files: Array<MultipartFile>): List<String> {
        return files.map { file ->
            logger.info("------------------")
            logger.info("name : ${file.name}")
            logger.info("origin name : ${file.originalFilename}")
            logger.info("type : ${file.contentType}")

            validateImageFile(file)

            val uuid = UUID.randomUUID().toString()
            val saveFilename = "${uuid}_${file.originalFilename}"
            val savePath = Paths.get(uploadPath, saveFilename)

            try {
                saveFile(file, savePath)
                createThumbnail(savePath)
                saveFilename // 저장된 파일명 반환
            } catch (e: IOException) {
                logger.error("파일 업로드 실패: ${e.message}")
                throw UploadCustomException(ErrorCode.FILE_UPLOAD_FAILED)
            }
        }
    }

    private fun saveFile(file: MultipartFile, savePath: Path) {
        try {
            file.transferTo(savePath.toFile()) // 실제 파일 저장 수행
        } catch (e: IOException) {
            logger.error("파일 저장 실패: ${e.message}")
            throw UploadCustomException(ErrorCode.FILE_UPLOAD_FAILED)
        }
    }

    private fun validateImageFile(file: MultipartFile) {
        if (file.isEmpty || !isImageFile(file)) {
            throw UploadCustomException(ErrorCode.INVALID_FILE_FORMAT)
        }
    }

    private fun isImageFile(file: MultipartFile): Boolean {
        return file.contentType?.startsWith("image/") ?: false
    }

    private fun createThumbnail(savePath: Path) {
        try {
            val thumbnailFilename = "s_${savePath.fileName}"
            // 썸네일 파일 생성
            Thumbnails.of(savePath.toFile())
                .size(150, 150)
                .toFile(File(uploadPath, thumbnailFilename))
            logger.info("썸네일 생성 완료: $thumbnailFilename")
        } catch (e: IOException) {
            logger.error("썸네일 생성 실패: ${e.message}")
            throw UploadCustomException(ErrorCode.THUMBNAIL_CREATION_FAILED)
        }
    }

    fun deleteFile(filename: String) {
        val file = File(uploadPath, filename)
        val thumbFile = File(uploadPath, "s_$filename") // 썸네일 파일 이름 생성

        // 원본 파일과 썸네일 파일이 모두 존재하지 않는 경우
        if (!file.exists() && !thumbFile.exists()) {
            logger.warn("파일을 찾을 수 없음: $filename")
            throw UploadCustomException(ErrorCode.FILE_NOT_FOUND)
        }

        try {
            deleteIfExists(file, "파일")
            deleteIfExists(thumbFile, "썸네일")
        } catch (e: Exception) {
            logger.error("파일 삭제 실패: ${e.message}")
            throw UploadCustomException(ErrorCode.FILE_DELETE_FAILED)
        }
    }

    private fun deleteIfExists(file: File, fileType: String) {
        if (file.exists() && file.delete()) {
            logger.info("$fileType 삭제 완료: ${file.name}")
        } else {
            logger.warn("$fileType 존재하지 않음: ${file.name}")
        }
    }
}
