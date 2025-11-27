<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>관리자 OTP 인증 | 대기질 정보</title>

    <!-- 폰트 -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap" rel="stylesheet">

    <!-- 기존 로그인 스타일 재사용 -->
    <link rel="stylesheet" href="/css/login.css">

    <style>
        /* OTP 입력창 확대 */
        .otp-input {
            letter-spacing: 6px;
            font-size: 24px;
            text-align: center;
            font-weight: bold;
        }
    </style>
</head>

<body>
    <!-- Header -->
    <header>
        <nav class="nav" aria-label="주요 메뉴">
            <a href="/main" class="brand">대기질 정보</a>
            <div class="nav-right">
                <a href="/login">로그인</a>
                <a href="/register">회원가입</a>
                <a href="/admin/login" aria-current="page" style="font-weight:700; color:var(--brand)">관리자정보</a>
            </div>
        </nav>
    </header>

    <!-- Promo Bar -->
    <div class="promo" aria-hidden="true"></div>

    <!-- OTP 인증 페이지 -->
    <main class="auth-wrap">
        <section class="auth-card" aria-label="관리자 OTP 인증">
            <div class="auth-form">
                <h1 class="auth-title">OTP 인증</h1>
                <p class="auth-desc" style="line-height:1.6;">
                    관리자 이메일로 발송된 <strong>6자리 OTP 인증번호</strong>를 입력하세요.<br>
                    보안 강화를 위해 OTP 인증이 필요합니다.
                </p>

                <!-- 오류 메시지 -->
                <c:if test="${not empty otp_err}">
                    <div class="alert alert-danger" style="color:red; margin-bottom:10px; font-size:13px;">
                        ${otp_err}
                    </div>
                </c:if>

                <!-- OTP 입력 폼 -->
                <form id="otpForm" method="post" action="/admin/otpCheck">
                    <div class="field">
                        <label class="label" for="otp">OTP 번호</label>
                        <input 
                            class="input otp-input" 
                            id="otp" 
                            name="otp" 
                            type="text"
                            maxlength="6"
                            placeholder="------"
                            required
                            autocomplete="one-time-code"
                        />
                    </div>

                    <div class="submit">
                        <button type="submit" class="btn btn-primary">인증하기</button>
                        <button type="button" class="btn btn-ghost" onclick="location.href='/admin/login'">돌아가기</button>
                    </div>
                </form>

                <div style="margin-top:15px; text-align:center;">
                    <button class="btn btn-small" onclick="resendOTP()" style="border:none; color:#0077ff; background:none;">
                        인증번호 다시 받기
                    </button>
                </div>
            </div>
        </section>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <h2>대기질 정보 시스템</h2>
        <p>대기질 정보 시스템 | 데이터 출처: 공공데이터포털 (data.go.kr)</p>
        <p>환경부 실시간 대기질 정보 제공</p>
        <p>주소: 부산시 부산진구 범내골</p>
        <br>
        <a href="#">이용약관</a>
        <a href="#">개인정보처리방침</a>
    </footer>

	<script>
	    // OTP 숫자만 입력
	    document.getElementById("otp").addEventListener("input", function () {
	        this.value = this.value.replace(/[^0-9]/g, "");
	    });

	    // --- ⏱ 타이머 ---
	    let expireAt = Number("${sessionScope.adminOTPExpireAt == null ? 0 : sessionScope.adminOTPExpireAt}");
	    let timerInterval = null;

	    // 타이머 박스 생성
	    const timerBox = document.createElement("div");
	    timerBox.id = "otp-timer-box";
	    timerBox.style.textAlign = "center";
	    timerBox.style.marginTop = "8px";
	    timerBox.style.fontSize = "14px";
	    timerBox.style.fontWeight = "bold";
	    timerBox.style.color = "#333";
	    timerBox.textContent = "남은 시간: --:--";

	    document.querySelector(".auth-form").appendChild(timerBox);

	    function startOTPTimer(newExpireAt) {

	        // 새 expireAt 적용
	        if (newExpireAt) expireAt = newExpireAt;

	        // 기존 타이머 중복 제거
	        if (timerInterval) clearInterval(timerInterval);

	        function update() {
	            const now = Date.now();
	            const diff = expireAt - now;

	            if (diff <= 0) {
	                timerBox.textContent = "만료됨";
	                alert("OTP 유효시간이 만료되었습니다. 다시 로그인해주세요.");
	                location.href = "/admin/login";
	                return;
	            }

	            const min = Math.floor(diff / 60000);
	            const sec = Math.floor((diff % 60000) / 1000);
	            timerBox.textContent =
	                "남은 시간: " + min + ":" + (sec < 10 ? "0" + sec : sec);
	        }

	        update();
	        timerInterval = setInterval(update, 1000);
	    }

	    // 최초 페이지 로드 시 타이머 시작
	    if (expireAt > 0) startOTPTimer();


	    // --- ✉ 인증번호 재전송 ---
	    function resendOTP() {

	        fetch("/admin/resendOTP", {
	            method: "POST",
	            credentials: "include"
	        })
	        .then(res => res.json())
	        .then(result => {

	            if (result.status === "success") {
	                alert("새 OTP 인증번호가 이메일로 전송되었습니다.");

	                // 🔥 타이머 초기화 — 새 expireAt 적용
	                startOTPTimer(result.expireAt);

	            } else {
	                alert("세션이 만료되었습니다. 다시 로그인해주세요.");
	                location.href = "/admin/login";
	            }
	        })
	        .catch(err => {
	            console.error(err);
	            alert("오류가 발생했습니다.");
	        });
	    }
	</script>


</body>
</html>
