package com.example.echo.global.util

import com.example.echo.global.exception.UploadException
import jakarta.annotation.PostConstruct
import net.coobird.thumbnailator.Thumbnails
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

    @Value("\${com.example.upload.path}")
    lateinit var uploadPath: String

    @PostConstruct
    fun init() {
        val tempDir = File(uploadPath)

        // 업로드 디렉토리가 없으면 생성
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw RuntimeException("업로드 디렉토리 생성 실패: $uploadPath")
        }

        // 업로드 경로가 디렉토리인지 확인
        if (!tempDir.isDirectory) {
            throw RuntimeException("업로드 경로가 유효하지 않습니다: $uploadPath")
        }

        uploadPath = tempDir.absolutePath
        println("--- uploadPath : $uploadPath") // log.info 대신 println 사용
    }

    fun upload(file: MultipartFile): String {
        // 파일 타입 확인
        if (file.isEmpty || !isImageFile(file)) {
            throw UploadException("업로드할 파일은 이미지 파일이어야 합니다.")
        }

        val fileName = "${System.currentTimeMillis()}_${file.originalFilename}"
        val destinationFile = File(uploadPath, fileName)

        try {
            file.transferTo(destinationFile) // 파일 저장
        } catch (e: IOException) {
            throw UploadException("파일 업로드에 실패했습니다.", e)
        }

        return destinationFile.path // 저장된 파일의 경로 반환
    }

    // 여러 파일 업로드
    fun upload(files: Array<MultipartFile>): List<String> {
        val filenames = mutableListOf<String>()

        for (file in files) {
            println("------------------")
            println("name : ${file.name}")
            println("origin name : ${file.originalFilename}")
            println("type : ${file.contentType}")

            // 파일 타입 확인
            if (!isImageFile(file)) {
                println("--- 지원하지 않는 파일 타입 : ${file.originalFilename}")
                throw UploadException("모든 파일은 이미지 파일이어야 합니다: ${file.originalFilename}")
            }

            val uuid = UUID.randomUUID().toString()
            val saveFilename = "${uuid}_${file.originalFilename}"
            val savePath = Paths.get(uploadPath, saveFilename)

            try {
                saveFile(file, savePath)
                createThumbnail(savePath, uuid)
                filenames.add(saveFilename)
            } catch (e: IOException) {
                println("파일 업로드 중 오류 발생: ${e.message}")
                throw UploadException("파일 업로드 중 오류가 발생했습니다: ${file.originalFilename}", e)
            }
        }
        return filenames
    }

    private fun saveFile(file: MultipartFile, savePath: Path) {
        file.transferTo(savePath.toFile())
    }

    // 이미지 파일인지 확인하는 메서드
    private fun isImageFile(file: MultipartFile): Boolean {
        val contentType = file.contentType
        return contentType != null && contentType.startsWith("image/")
    }

    // 썸네일 파일 생성
    private fun createThumbnail(savePath: Path, uuid: String) {
        try {
            val originalFilename = savePath.fileName.toString()
            val thumbnailFilename = "s_$originalFilename"

            // 썸네일 파일 생성
            Thumbnails.of(File(savePath.toString()))
                .size(150, 150)
                .toFile(File(uploadPath, thumbnailFilename))

            println("썸네일 생성 성공: $thumbnailFilename") // log.info 대신 println 사용
        } catch (e: IOException) {
            println("썸네일 생성 중 오류 발생: ${e.message}")
        }
    }

    // 업로드 파일 삭제
    fun deleteFile(filename: String) {
        val file = File(uploadPath, filename)
        val thumbFilename = "s_$filename" // 썸네일 파일 이름 생성
        val thumbFile = File(uploadPath, thumbFilename)

        try {
            if (file.exists() && file.delete()) {
                println("파일 삭제 성공: $filename") // log.info 대신 println 사용
            } else {
                println("삭제할 파일이 존재하지 않음: $filename") // log.warn 대신 println 사용
            }

            if (thumbFile.exists() && thumbFile.delete()) {
                println("썸네일 삭제 성공: $thumbFilename") // log.info 대신 println 사용
            } else {
                println("삭제할 썸네일 파일이 존재하지 않음: $thumbFilename") // log.warn 대신 println 사용
            }
        } catch (e: Exception) {
            println("파일 삭제 중 오류 발생: ${e.message}") // log.error 대신 println 사용
        }
    }
}
