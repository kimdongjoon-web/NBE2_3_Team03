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
- ...

<br>

## 🛠 개발 환경

| **구성 요소**      | **설명**                                               |
|----------------|------------------------------------------------------|
| **JDK**        | Java 17                                              |
| **프레임워크**      | Spring Boot 3.3.5                                    |
| **DB**         | MySQL                                                |
| **빌드 도구**      | Gradle                                               |
| **IDE**        | IntelliJ IDEA                                        |
| **기술 및 라이브러리** | Redis, JWT, React, Spring Security, Chatgpt API, JPA |
| **협업 도구**      | Notion, GitHub, Slack                                |

<br>

## 📁구조 토글

<details>
  <summary>📦 패키지 구조 </summary>
📦src
 ┣ main<br/>
 ┃ ┣ java<br/>
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
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ service<br/>
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
 ┃ ┗ java<br/>
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

<br>

## 💻시스템 아키텍처
![3차프로젝트_8](https://github.com/user-attachments/assets/efedc8f2-48ef-4918-aed9-50a744a09bab)
<br>

<br>

## 🎞API 명세서

<br>
