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
        @Parameter(description = "회원 로그인 요청 정보", required = true)
        @Valid @RequestBody loginRequest: MemberLoginRequest
    ): ResponseEntity<ApiResponse<Map<String, String>>> =
        ResponseEntity.ok(ApiResponse.success(memberService.login(loginRequest)))

    // 회원 등록
    @Operation(summary = "회원 등록", description = "신규 회원을 등록합니다.")
    @PostMapping("/signup")
    fun createMember(
        @Parameter(description = "신규 회원 등록 요청 정보", required = true)
        @Valid @RequestBody memberRequest: MemberCreateRequest
    ): ResponseEntity<ApiResponse<MemberResponse>> =
        ResponseEntity.ok(ApiResponse.success(memberService.createMember(memberRequest)))

    // 회원 조회 (관리자용)
    @Operation(summary = "회원 조회", description = "관리자가 회원 번호로 회원 정보를 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{memberId}")
    fun getMember(
        @Parameter(description = "조회할 회원의 ID", required = true)
        @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<MemberResponse>> =
        ResponseEntity.ok(ApiResponse.success(memberService.getMember(memberId)))

    // 전체 회원 조회 (관리자용)
    @Operation(summary = "회원 전체 조회", description = "관리자가 전체 회원 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getAllMembers(): ResponseEntity<ApiResponse<List<MemberResponse>>> =
        ResponseEntity.ok(ApiResponse.success(memberService.getAllMembers()))

    // 회원 정보 수정
    @Operation(summary = "회원 정보 수정", description = "회원이 자신의 정보를 수정합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @PutMapping("/{memberId}")
    fun updateMember(
        @Parameter(description = "수정할 회원의 ID", required = true)
        @PathVariable memberId: Long,
        @Parameter(description = "회원 수정 요청 정보", required = true)
        @Valid @RequestBody updateRequest: MemberUpdateRequest
    ): ResponseEntity<ApiResponse<MemberResponse>> =
        ResponseEntity.ok(ApiResponse.success(memberService.updateMember(memberId, updateRequest)))

    // 회원 삭제
    @Operation(summary = "회원 삭제", description = "관리자가 회원을 삭제합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{memberId}")
    fun deleteMember(
        @Parameter(description = "삭제할 회원의 ID", required = true)
        @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        memberService.deleteMember(memberId)
        return ResponseEntity.ok(ApiResponse.success(Unit))
    }

    // 비밀번호 변경
    @Operation(summary = "비밀번호 변경", description = "회원의 비밀번호를 변경합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @PutMapping("/{memberId}/password")
    fun updatePassword(
        @Parameter(description = "비밀번호를 변경할 회원의 ID", required = true)
        @PathVariable memberId: Long,
        @Parameter(description = "비밀번호 변경 요청 정보", required = true)
        @Valid @RequestBody updateRequest: MemberUpdateRequest
    ): ResponseEntity<ApiResponse<MemberResponse>> =
        ResponseEntity.ok(ApiResponse.success(memberService.updateMember(memberId, updateRequest)))

    // 프로필 이미지 조회
    @Operation(summary = "프로필 이미지 조회", description = "회원의 프로필 이미지를 조회합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @GetMapping("/{memberId}/avatar")
    fun getAvatar(
        @Parameter(description = "조회할 회원의 ID", required = true)
        @PathVariable memberId: Long
    ): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.ok(ApiResponse.success(memberService.getAvatar(memberId)))

    // 프로필 이미지 업로드
    @Operation(summary = "프로필 이미지 업로드", description = "회원의 프로필 이미지를 업로드합니다.")
    @PreAuthorize("authentication.principal.memberId == #memberId")
    @PostMapping("/{memberId}/avatar")
    fun uploadAvatar(
        @Parameter(description = "업로드할 회원의 ID", required = true)
        @PathVariable memberId: Long,
        @Parameter(description = "업로드할 프로필 이미지 파일", required = true)
        @RequestParam("avatarImage") avatarImage: MultipartFile
    ): ResponseEntity<ApiResponse<MemberResponse>> =
        ResponseEntity.ok(ApiResponse.success(
            memberService.updateAvatar(memberId, ProfileImageUpdateRequest(avatarImage))
        ))

    // 보호된 회원 정보 조회
    @Operation(summary = "보호된 회원 정보 조회", description = "인증된 회원의 정보를 조회합니다.")
    @GetMapping("/protected-data")
    fun getProtectedData(
        @Parameter(description = "현재 인증된 사용자 정보", required = true)
        authentication: Authentication
    ): ResponseEntity<ApiResponse<MemberResponse>> =
        ResponseEntity.ok(ApiResponse.success(memberService.getMemberInfo(authentication)))
}
