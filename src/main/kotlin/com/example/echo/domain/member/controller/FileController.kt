package com.example.echo.domain.member.controller

import com.example.echo.global.api.ApiResponse
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.UploadCustomException
import com.example.echo.global.util.UploadUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Controller", description = "파일 업로드 및 삭제 관리 API")
class FileController(
    private val uploadUtil: UploadUtil
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "파일 업로드", description = "파일을 업로드합니다.")
    @PostMapping("/upload")
    fun uploadFile(
        @Parameter(description = "업로드할 파일", required = true)
        @RequestParam("files") files: Array<MultipartFile>
    ): ResponseEntity<ApiResponse<List<String>>> {
        log.info("--- uploadFile() invoked ---")

        // 업로드 파일이 없는 경우
        if (files.isEmpty()) {
            throw UploadCustomException(ErrorCode.FILE_NOT_FOUND)
        }

        //각 파일에 대해 확장자 체크 및 로그 기록
        files.forEach { file ->
            val originalFilename = file.originalFilename
            log.debug("Checking file extension for: $originalFilename")
            checkFileExt(originalFilename)
        }

        // 업로드 수행
        val uploadedFiles = uploadUtil.upload(files)

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(uploadedFiles))
    }

    // 업로드 파일 확장자 체크
    private fun checkFileExt(filename: String?) {
        if (filename.isNullOrEmpty()) {
            throw UploadCustomException(ErrorCode.FILE_NOT_FOUND)
        }
        // "." 이후의 문자열을 확장자로 추출, 파일 이름에 "."이 없다면 빈 문자열 반환
        val ext = filename.substringAfterLast(".", "")

        val regExp = "(?i)^(jpg|jpeg|png|gif|bmp)$".toRegex()

        if (!ext.matches(regExp)) {
            throw UploadCustomException(ErrorCode.INVALID_FILE_FORMAT)
        }
    }

    @Operation(summary = "파일 삭제", description = "파일 이름으로 파일을 삭제합니다.")
    @DeleteMapping("/{filename}")
    fun fileDelete(
        @Parameter(description = "삭제할 파일 이름", required = true)
        @PathVariable filename: String
    ): ResponseEntity<ApiResponse<String>> {
        log.info("--- fileDelete() invoked for filename: $filename")

        return try {
            uploadUtil.deleteFile(filename)
            log.info("파일이 성공적으로 삭제되었습니다: $filename")
            ResponseEntity.ok(ApiResponse.success("파일이 성공적으로 삭제되었습니다.", filename))
        } catch (e: NoSuchFileException) {
            log.error("파일을 찾을 수 없음: $filename")
            throw UploadCustomException(ErrorCode.FILE_NOT_FOUND)
        } catch (e: SecurityException) {
            log.error("파일 삭제 권한 없음: $filename")
            throw UploadCustomException(ErrorCode.INSUFFICIENT_PERMISSIONS)
        } catch (e: Exception) {
            log.error("파일 삭제 중 오류 발생: ${e.message}")
            throw UploadCustomException(ErrorCode.FILE_DELETE_FAILED)
        }
    }
}