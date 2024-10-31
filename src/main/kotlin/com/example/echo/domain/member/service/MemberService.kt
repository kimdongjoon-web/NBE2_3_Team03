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
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository,
    private val uploadUtil: UploadUtil,
    private val jwtUtil: JWTUtil
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
        val member = memberRequest.toMember().apply {
            password = passwordEncoder.encode(password) // password 암호화
        }
        val savedMember = memberRepository.save(member)
        return MemberResponse.from(savedMember)
    }

    // 회원 조회
    fun getMember(memberId: Long): MemberResponse {
        return MemberResponse.from(findMemberById(memberId))
    }

    // 전체 회원 조회
    fun getAllMembers(): List<MemberResponse> {
        return memberRepository.findAll().map { MemberResponse.from(it) }
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

        // MemberUpdateRequest를 사용하여 업데이트
        memberRequest.updateMember(member)

        return MemberResponse.from(memberRepository.save(member)) // 수정된 회원 정보 저장
    }

    // 회원 삭제
    @Transactional
    fun deleteMember(memberId: Long) {
        memberRepository.delete(findMemberById(memberId))
    }

    // 프로필 사진 조회
    fun getAvatar(memberId: Long): String? {
        return findMemberById(memberId).avatarImage
    }

    // 프로필 사진 업데이트
    @Transactional
    fun updateAvatar(id: Long, requestDto: ProfileImageUpdateRequest): MemberResponse {
        val member = findMemberById(id)
        val avatarUrl = uploadUtil.upload(requestDto.avatarImage)
        member.avatarImage = avatarUrl
        memberRepository.save(member)

        return MemberResponse.from(member) // MemberResponse로 반환
    }

    // userID로 회원 조회
    private fun findMemberByUserId(userId: String): Member {
        return memberRepository.findByUserId(userId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }
    }

    // password 검증
    private fun validatePassword(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND)
        }
    }

    // 공통 메서드: 회원 ID로 회원 조회
    private fun findMemberById(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { PetitionCustomException(ErrorCode.MEMBER_NOT_FOUND) }
    }

    // JWT 토큰 생성
    private fun makeToken(member: Member): Map<String, String> {
        val payloadMap = member.getPayload()
        val accessToken = jwtUtil.createToken(payloadMap, 60) // 60분 유효
        val refreshToken = jwtUtil.createToken(mapOf("userId" to member.userId), 60 * 24 * 7) // 7일 유효
        return mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }

    // 이메일 중복 확인
    private fun checkEmailDuplicate(email: String) {
        if (memberRepository.findByEmail(email).isPresent) {
            throw PetitionCustomException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }
    }

    // 전화번호 중복 확인
    private fun checkPhoneDuplicate(phone: String) {
        if (memberRepository.findByPhone(phone).isPresent) {
            throw PetitionCustomException(ErrorCode.PHONE_ALREADY_EXISTS)
        }
    }

    // 보호된 데이터 요청 시 사용자 정보 조회
    fun getMemberInfo(authentication: Authentication): MemberResponse {
        val userId = authentication.name // 인증된 사용자 ID 가져오기
        val member = findMemberByUserId(userId) // 사용자 정보를 DB에서 조회
        return MemberResponse.from(member) // MemberResponse로 변환하여 반환
    }
}
