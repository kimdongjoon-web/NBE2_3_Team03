package com.example.echo.domain.member.service

import com.example.echo.domain.member.dto.request.MemberCreateRequest
import com.example.echo.domain.member.dto.request.MemberLoginRequest
import com.example.echo.domain.member.dto.request.MemberUpdateRequest
import com.example.echo.domain.member.dto.request.ProfileImageUpdateRequest
import com.example.echo.domain.member.dto.response.MemberResponse
import com.example.echo.domain.member.entity.Member
import com.example.echo.domain.member.repository.MemberRepository
import com.example.echo.global.exception.ErrorCode
import com.example.echo.global.exception.PetitionCustomException
import com.example.echo.global.security.util.JWTUtil
import com.example.echo.global.util.UploadUtil
import org.modelmapper.ModelMapper
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository,
    private val uploadUtil: UploadUtil,
    private val jwtUtil: JWTUtil,
    private val modelMapper: ModelMapper
) {
    // 회원 로그인
    fun login(memberRequest: MemberLoginRequest): Map<String, String> {
        val member = findMemberByUserId(memberRequest.userId)
        validatePassword(memberRequest.password, member.password)
        return makeToken(member)
    }

    // 회원 등록
    @Transactional
    fun createMember(memberRequest: MemberCreateRequest): MemberResponse {

        checkUserIdDuplicate(memberRequest.userId)
        checkEmailDuplicate(memberRequest.email)
        checkPhoneDuplicate(memberRequest.phone)

        val member = memberRequest.toMember().apply {
            password = passwordEncoder.encode(password)
        }
        return modelMapper.map(memberRepository.save(member), MemberResponse::class.java)
    }

    // 회원 조회
    fun getMember(memberId: Long): MemberResponse {
        val member = findMemberById(memberId)
        return modelMapper.map(member, MemberResponse::class.java)
    }

    // 전체 회원 조회
    fun getAllMembers(): List<MemberResponse> {
        return memberRepository.findAll()
            .map { modelMapper.map(it, MemberResponse::class.java) }
    }

    // 회원 정보 수정
    @Transactional
    fun updateMember(memberId: Long, memberRequest: MemberUpdateRequest): MemberResponse {
        val member = findMemberById(memberId)

        // 이메일 중복 확인 (이메일이 변경되었을 때만 확인)
        if (member.email != memberRequest.email) {
            checkEmailDuplicate(memberRequest.email)
        }

        // 전화번호 중복 확인 (전화번호가 변경되었을 때만 확인)
        if (member.phone != memberRequest.phone) {
            checkPhoneDuplicate(memberRequest.phone)
        }

        // 비밀번호 변경 시 추가 검증
        memberRequest.newPassword?.let { newPassword ->
            val currentPassword =
                memberRequest.currentPassword ?: throw PetitionCustomException(ErrorCode.INVALID_PASSWORD)

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, member.password)) {
                throw PetitionCustomException(ErrorCode.INVALID_OLD_PASSWORD)
            }

            // 새 비밀번호가 현재 비밀번호와 같은지 확인
            if (passwordEncoder.matches(newPassword, member.password)) {
                throw PetitionCustomException(ErrorCode.SAME_AS_OLD_PASSWORD)
            }

            member.password = passwordEncoder.encode(newPassword)
        }

        // MemberUpdateRequest 사용하여 업데이트
        memberRequest.updateMember(member)
        return modelMapper.map(memberRepository.save(member), MemberResponse::class.java)
    }

    // 회원 삭제
    @Transactional
    fun deleteMember(memberId: Long) {
        memberRepository.delete(findMemberById(memberId))
    }

    // 프로필 사진 조회
    fun getAvatar(memberId: Long): String {
        return findMemberById(memberId).avatarImage
            ?: throw PetitionCustomException(ErrorCode.AVATAR_NOT_FOUND)
    }

    // 프로필 사진 업데이트
    @Transactional
    fun updateAvatar(id: Long, requestDto: ProfileImageUpdateRequest): MemberResponse {
        val member = findMemberById(id).apply {
            avatarImage = uploadUtil.upload(requestDto.avatarImage)
        }
        return modelMapper.map(memberRepository.save(member), MemberResponse::class.java)
    }

    // 보호된 데이터 요청 시 사용자 정보 조회
    fun getMemberInfo(authentication: Authentication): MemberResponse {
        val userId = authentication.name // 인증된 사용자 ID 가져오기
        val member = findMemberByUserId(userId) // 사용자 정보를 DB에서 조회
        return modelMapper.map(member, MemberResponse::class.java) // MemberResponse로 변환하여 반환
    }

    /**
     * ===========================================================================
     * Helper Methods
     * ===========================================================================
     */

    // userId로 회원 조회
    internal fun findMemberByUserId(userId: String): Member {
        return memberRepository.findByUserId(userId)
            ?: throw PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND)
    }

    // memberId로 회원 조회
    internal fun findMemberById(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }
    }

    // userId 중복 확인
    internal fun checkUserIdDuplicate(userId: String) {
        if (memberRepository.findByUserId(userId) != null) {
            throw PetitionCustomException(ErrorCode.USERID_ALREADY_EXISTS)
        }
    }

    // 이메일 중복 확인
    internal fun checkEmailDuplicate(email: String) {
        memberRepository.findByEmail(email)?.let {
            throw PetitionCustomException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }
    }

    // 전화번호 중복 확인
    internal fun checkPhoneDuplicate(phone: String) {
        memberRepository.findByPhone(phone)?.let {
            throw PetitionCustomException(ErrorCode.PHONE_ALREADY_EXISTS)
        }
    }

    // 비밀번호 검증
    internal fun validatePassword(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw PetitionCustomException(ErrorCode.INVALID_PASSWORD)
        }
    }

    // JWT 토큰 생성
    internal fun makeToken(member: Member): Map<String, String> {
        val accessToken = jwtUtil.createToken(member.getPayload(), 60) // 60분 유효
        val refreshToken = jwtUtil.createToken(mapOf("userId" to member.userId), 60 * 24 * 7) // 7일 유효

        return mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }
}
