# 📢NBB2_3_Team03 (Echo)

**국민의 정책 참여를 촉진하고 청원이 더 널리 퍼질 수 있도록 기여하고자 합니다!**

<br>

## 😀팀원 소개

<table>
  <tr>
    <td>
        <a href="https://github.com/juchan204">
            <img src="https://avatars.githubusercontent.com/u/127003137?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/jooinjoo">
            <img src="https://avatars.githubusercontent.com/u/177445328?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/Song-min-geun">
            <img src="https://avatars.githubusercontent.com/u/164311387?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/kimdongjoon-web">
            <img src="https://avatars.githubusercontent.com/u/176230828?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/usernameme0w">
            <img src="https://avatars.githubusercontent.com/u/163955522?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/Wenpe77">
            <img src="https://avatars.githubusercontent.com/u/105703574?v=4" width="100px" />
        </a>
    </td>
  </tr>
  <tr>
    <td><b>이주찬</b></td>
    <td><b>정원주</b></td>
    <td><b>송민근</b></td>
    <td><b>김동준</b></td>
    <td><b>강수민</b></td>
    <td><b>위성운</b></td>
  </tr>
  <tr>
    <td><b>Backend,Frontend</b></td>
    <td><b>Backend,Frontend</b></td>
    <td><b>Backend</b></td>
    <td><b>Backend</b></td>
    <td><b>Backend</b></td>
    <td><b>Backend</b></td>
  </tr>
</table>
<br>

## 📝서비스 소개

국민동의 청원 알리미"는 국민이 청원 정보를 보다 쉽게 접할 수 있도록 돕는 서비스입니다.
사용자는 **동의가 급증하거나 만료가 임박한 청원을 확인**하고, **AI 요약을 통해 청원의 핵심 내용을 간편하게 파악할 수 있습니다.**

또한, **좋아요 수가 많은 청원**을 통해 **대중의 관심이 집중된 청원**을 쉽게 찾을 수 있으며, 사용자는 관심 있는 청원을 **관심목록에 저장**하고, 관련 소식을 지속적으로 받아볼 수 있습니다.

<br>

## 🧾**개발 기간**

2024/10/29 ~ 2024/11/06

<br>

## ✔주요기능

- Chatgpt API를 이용한 청원 요약 안내
- JWT 기반 인증 시스템
- 청원 목록 조회
- 관심 청원 목록 조회
- 청원 관련 뉴스 제공
- 청원 동의 수 및 관심도에 따른 추천
- 청원 좋아요 수에 따른 추천
- 나이대별 관심 청원 순위 제공
- 

<br>

## 🛠 개발 환경

| **구성 요소**      | **설명**                                               |
|----------------|------------------------------------------------------|
| **JDK**        | Kotlin                                       |
| **프레임워크**      | Spring Boot 3.3.5                                    |
| **DB**         | MySQL, H2                                               |
| **빌드 도구**      | Gradle                                               |
| **IDE**        | IntelliJ IDEA                                        |
| **기술 및 라이브러리** | Redis, JWT, Spring Security, Chatgpt API, JPA, Naver API |
| **협업 도구**      | Notion, GitHub, Slack                                |

<br>

## 📁구조 토글

<details>
  <summary>📦 패키지 구조 </summary>
📦src
 ┣ main<br/>
 ┃ ┣ kotlin<br/>
 ┃ ┃ ┗ com<br/>
 ┃ ┃ ┃ ┗ example<br/>
 ┃ ┃ ┃ ┃ ┗ echo<br/>
 ┃ ┃ ┃ ┃ ┃ ┣ domain<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ inquiry<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ controller<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ dto<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ request<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ response<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ entity<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ repository<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ service<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ interest<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ entity<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ member<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ controller<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ advice<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ dto<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ request<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ response<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ entity<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ repository<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ service<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ petition<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ controller<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ crawling<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ dto<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ request<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ response<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ entity<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ repository<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ service<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ util<br/>
 ┃ ┃ ┃ ┃ ┃ ┣ global<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ advice<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ api<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ config<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ exception<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ security<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ auth<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ filter<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ util<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ util<br/>
 ┃ ┣ react<br/>
 ┃ ┃ ┣ public<br/>
 ┃ ┃ ┣ src<br/>
 ┃ ┃ ┃ ┣ assets<br/>
 ┃ ┃ ┃ ┣ components<br/>
 ┃ ┃ ┃ ┣ css<br/>
 ┃ ┗ resources<br/>
 ┃ ┃ ┣ static<br/>
 ┃ ┃ ┃ ┗ images<br/>
 ┗ test<br/>
 ┃ ┗ kotlin<br/>
 ┃ ┃ ┗ com<br/>
 ┃ ┃ ┃ ┗ example<br/>
 ┃ ┃ ┃ ┃ ┗ echo<br/>
 ┃ ┃ ┃ ┃ ┃ ┣ domain<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ inquiry<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ repository<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ service<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ member<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ repository<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ service<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ petition<br/>
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ service<br/>
</details>
<details>
  <summary>📦 ERD - Kotlin </summary>
<img alt="echo_ERD" src="https://github.com/user-attachments/assets/124f11c0-44fa-434d-b39f-2f2d77233508">
</details>

<details>
  <summary>📦클래스 다이어그램</summary>
<img alt="스크린샷 2024-11-05 오후 2 11 02" src="https://github.com/user-attachments/assets/59d4e937-42b6-41f9-9227-389f2e38d933">
</details>
<details>
 <summary>📦시퀀스 다이어그램</summary>
1. 청원 등록
<img alt = "1" src = "https://github.com/user-attachments/assets/42dd00c4-3f40-4ced-ab98-7803fb42a5d5">
2. 청원 단건 조회
<img alt = "2청원 단건 조회" src = "https://github.com/user-attachments/assets/5ce1ae52-a28a-404c-bf3a-0386696a7b15">
3. 청원 전체 목록 조회
<img alt = "3청원 전체 목록 조회" src="https://github.com/user-attachments/assets/505d1dfa-a41f-45fd-ad2c-bd06dd5aa20a">
4. 청원 좋아요 순 조회
<img alt = "4청원 좋아요 순 조회" src="https://github.com/user-attachments/assets/420696bb-e62a-4c9e-9b08-0c701b0767fb">
5. 청원 관심목록 수 기준 조회
<img alt = "5청원 관심목록 수 기준 조회" src="https://github.com/user-attachments/assets/157dba74-c263-4cdc-b9c5-705281b24d0e">
6. 청원 카테고리별 조회
<img alt ="6청원 카테고리별 조회" src="https://github.com/user-attachments/assets/66d73e46-2cfd-4085-8454-56a71f30cd26">
7. 청원 만료일 순 조회
<img alt ="7청원 만료일 순 조회" src ="https://github.com/user-attachments/assets/973f7134-20df-46c7-b69d-2d266dc3bba4">
8. 동의자 수 급증 청원 조회
<img alt = "8동의자 수 급증 청원 조회" src="https://github.com/user-attachments/assets/09ef4283-0632-4d85-a170-6e6d31b24208">
9. 나이대별 청원 추천 조회
<img alt = "9나이대별 청원 추천 조회" src="https://github.com/user-attachments/assets/cbf0e4f0-5df6-4e5e-8caa-d9ee3f77435c">
10. 제목으로 청원 검색
<img alt="10제목으로 청원 검색" src="https://github.com/user-attachments/assets/02bbb374-f6a1-410d-bf51-a91b1beef224">
11. 청원 좋아요 기능
<img alt="11청원 좋아요 기능" src="https://github.com/user-attachments/assets/cb610bf2-ad00-4038-b2ec-64507d0c1bc8">
12. 청원 관심 목록 추가
<img alt="12청원 관심 목록 추가" src="https://github.com/user-attachments/assets/1f47bbc5-a93b-464e-a92b-640bd1c56605">
13. 청원 관심 목록 제거
<img alt="13청원 관심 목록 제거" src="https://github.com/user-attachments/assets/2de6a26e-6448-4d15-97ae-284dbd3e3f36">
14. 본인의 관심 목록 조회
<img alt="14본인의 관심 목록 조회" src="https://github.com/user-attachments/assets/dd870114-795a-4ba0-8850-bc4ddb71ad40">
15. 청원 수정
<img alt="15청원 수정" src="https://github.com/user-attachments/assets/5daf73d7-09f9-4199-8993-6d67dc3f5ebf">
16. 청원 삭제
<img alt="16 청원 삭제" src="https://github.com/user-attachments/assets/6b62feb2-6aee-4776-b848-9573dc235256">
</details>
<details>
  <summary>📦유스 케이스 다이어그램</summary>
문의, 뉴스
<img alt="문의뉴스UseCaseDiagram" src="https://github.com/user-attachments/assets/ee5ecd85-50ec-496d-850b-ae1733706fd0">
회원, 청원
<img alt="회원청원UseCaseDiagram" src="https://github.com/user-attachments/assets/cde00bec-224d-4c62-835a-d115b3178beb">
</details>
<details>
  <summary>📦플로우차트</summary>
<img alt="Flow Chart" src="https://github.com/user-attachments/assets/26c00076-417a-43ea-a396-a84616bb1350">
</details>
<br>

## 💻시스템 아키텍처
![3차프로젝트_8](https://github.com/user-attachments/assets/efedc8f2-48ef-4918-aed9-50a744a09bab)
<br>

