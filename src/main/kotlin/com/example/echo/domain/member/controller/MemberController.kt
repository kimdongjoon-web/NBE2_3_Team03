package com.example.echo.domain.member.controller

import com.example.echo.domain.member.dto.request.MemberCreateRequest
import com.example.echo.domain.member.dto.request.MemberLoginRequest
import com.example.echo.domain.member.dto.request.MemberUpdateRequest
import com.example.echo.domain.member.dto.request.ProfileImageUpdateRequest
import com.example.echo.domain.member.dto.response.MemberResponse
import com.example.echo.domain.member.service.MemberService
import com.example.echo.global.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member Controller", description = "회원 관리 API")
class MemberController(
    private val memberService: MemberService
) {

    // 로그인 API
    @Operation(summary = "회원 로그인", description = "회원이 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    fun loginMember(
        @Parameter(description = "회원 로그인 요청 정보", required = true) @Valid @RequestBody memberRequest: MemberLoginRequest
    ): ResponseEntity<ApiResponse<Map<String, String>>> {
        val token = memberService.login(memberRequest)
        return ResponseEntity.ok(ApiResponse.success(token))
    }

    // 회원 등록
    @Operation(summary = "회원 등록", description = "신규 회원을 등록합니다.")
    @PostMapping("/signup")
    fun createMember(
        @Parameter(description = "신규 회원 등록 요청 정보", required = true) @Valid @RequestBody memberRequest: MemberCreateRequest
    ): ResponseEntity<ApiResponse<MemberResponse>> {
        val createdMember = memberService.createMember(memberRequest)
        return ResponseEntity.ok(ApiResponse.success(createdMember))
    }

    // 관리자 memberId로 회원 조회
    @Operation(summary = "회원 조회", description = "관리자가 회원 번호를 통해 회원 정보를 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{memberId}")
    fun getMember(
        @Parameter(description = "조회할 회원의 ID", required = true) @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<MemberResponse>> {
        val foundMember = memberService.getMember(memberId)
        return ResponseEntity.ok(ApiResponse.success(foundMember))
    }

    // 관리자 회원 전체 조회
    @Operation(summary = "회원 전체 조회", description = "관리자가 전체 회원 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllMembers(): ResponseEntity<ApiResponse<List<MemberResponse>>> {
        val members = memberService.getAllMembers()
        return ResponseEntity.ok(ApiResponse.success(members))
    }

    // 회원 수정
    @Operation(summary = "회원 수정", description = "회원이 자신의 정보를 수정합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @PutMapping("/{memberId}")
    fun updateMember(
        @Parameter(description = "수정할 회원의 ID", required = true) @PathVariable memberId: Long,
        @Parameter(description = "회원 수정 요청 정보", required = true) @Valid @RequestBody memberRequest: MemberUpdateRequest
    ): ResponseEntity<ApiResponse<MemberResponse>> {
        val updatedMember = memberService.updateMember(memberId, memberRequest)
        return ResponseEntity.ok(ApiResponse.success(updatedMember))
    }

    // 관리자 회원 삭제
    @Operation(summary = "회원 삭제", description = "관리자가 회원 정보를 삭제합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{memberId}")
    fun deleteMember(
        @Parameter(description = "삭제할 회원의 ID", required = true) @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> { // Void 대신 Unit 사용
        memberService.deleteMember(memberId)
        return ResponseEntity.ok(ApiResponse.success(Unit)) // Unit 반환
    }

    // 회원 프로필 사진 조회
    @Operation(summary = "프로필 사진 조회", description = "회원이 자신의 프로필 사진을 조회합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @GetMapping("/{memberId}/avatar")
    fun getAvatar(
        @Parameter(description = "조회할 회원의 ID", required = true) @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<String>> {
        val avatarUrl = memberService.getAvatar(memberId) ?: "기본 아바타 URL" // null인 경우 기본값 설정
        return ResponseEntity.ok(ApiResponse.success(avatarUrl))
    }

    // 회원 프로필 사진 업로드
    @Operation(summary = "프로필 사진 업로드", description = "회원이 자신의 프로필 사진을 업로드합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @PostMapping("/{memberId}/avatar")
    fun uploadAvatar(
        @Parameter(description = "업로드할 회원의 ID", required = true) @PathVariable memberId: Long,
        @Parameter(description = "업로드할 프로필 사진 파일", required = true) @RequestParam("avatarImage") avatarImage: MultipartFile
    ): ResponseEntity<ApiResponse<MemberResponse>> {
        val requestDto = ProfileImageUpdateRequest(avatarImage) // 직접 생성자에서 초기화
        val responseDto = memberService.updateAvatar(memberId, requestDto)
        return ResponseEntity.ok(ApiResponse.success(responseDto))
    }

    // 보호된 데이터 요청
    @Operation(summary = "보호된 데이터 요청", description = "보호된 데이터를 요청합니다. 로그인된 회원 정보에 따라 조회 가능합니다.")
    @GetMapping("/protected-data")
    fun getProtectedData(
        @Parameter(description = "현재 인증된 사용자 정보", required = true) authentication: Authentication
    ): ResponseEntity<ApiResponse<MemberResponse>> {
        val foundMember = memberService.getMemberInfo(authentication)
        return ResponseEntity.ok(ApiResponse.success(foundMember))
    }
}
