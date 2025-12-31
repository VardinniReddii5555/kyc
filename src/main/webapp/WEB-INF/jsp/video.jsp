<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
        <html>

        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>
                <spring:message code="video.title" />
            </title>
            <link rel="stylesheet" href="/css/style.css">
            <script>
                // Prevent backtracking
                history.pushState(null, null, location.href);
                window.onpopstate = function () {
                    history.go(1);
                };
            </script>
        </head>

        <body>

            <div class="container center">
                <jsp:include page="../lang_dropdown.jsp" />

                <h3>
                    <spring:message code="video.header" />
                </h3>
                <div class="kyc-instructions-dropdown">

                    <div class="kyc-instructions-title" onclick="toggleInstructions()">
                        <spring:message code="video.instructions.title" />
                        <span id="arrow" class="arrow">▾</span>
                    </div>

                    <div id="kycInstructions" class="kyc-instructions-content">
                        <ul>
                            <li>
                                <spring:message code="video.Instruction1" />
                            </li>
                            <li>
                                <spring:message code="video.Instruction2" />
                            </li>
                            <li>
                                <spring:message code="video.Instruction3" />
                            </li>
                            <li>
                                <spring:message code="video.Instruction4" />
                            </li>
                            <li>
                                <spring:message code="video.Instruction5" />
                            </li>
                            <li>
                                <spring:message code="video.Instruction6" />
                            </li>
                            <li>
                                <spring:message code="video.Instruction7" />
                            </li>
                        </ul>
                    </div>

                </div>



                <!-- Live Camera -->
                <video id="liveVideo" width="380" height="240" autoplay muted></video>

                <!-- Recorded Preview -->
                <video id="recordedVideo" width="380" height="240" controls style="display:none;"></video>

                <!-- Recording Status -->
                <p id="recordingStatus" style="color:red; font-weight:bold; display:none;">
                    <spring:message code="video.status.recording" />
                </p>

                <!-- Timer -->
                <p id="timerText" class="note">
                    <spring:message code="video.time.prefix" /> 0
                    <spring:message code="video.time.suffix" />
                </p>

                <!-- Buttons -->
                <div style="margin-top:15px;">
                    <button id="startBtn" onclick="startRecording()">
                        <spring:message code="video.button.start" />
                    </button>
                </div>

                <div style="margin-top:10px;">
                    <button id="stopBtn" onclick="stopRecording()" disabled>
                        <spring:message code="video.button.stop" />
                    </button>
                </div>

                <div style="margin-top:10px;">
                    <button id="reRecordBtn" onclick="reRecord()" style="display:none;">
                        <spring:message code="video.button.rerecord" />
                    </button>
                </div>

                <!-- NEXT button (enabled only after recording) -->
                <form action="/review" method="get" style="margin-top:15px;">
                    <button id="nextBtn" type="submit" disabled>
                        <spring:message code="video.button.next" />
                    </button>
                </form>
            </div>


            <script>
                const MAX_VIDEO_DURATION = ${ maxVideoDuration }; // From server configuration
                let mediaRecorder;
                let recordedChunks = [];
                let stream;
                let seconds = 0;
                let intervalId;

                const liveVideo = document.getElementById("liveVideo");
                const recordedVideo = document.getElementById("recordedVideo");
                const startBtn = document.getElementById("startBtn");
                const stopBtn = document.getElementById("stopBtn");
                const reRecordBtn = document.getElementById("reRecordBtn");
                const recordingStatus = document.getElementById("recordingStatus");
                const timerText = document.getElementById("timerText");
                const nextBtn = document.getElementById("nextBtn");
                function toggleInstructions() {
                    const box = document.getElementById("kycInstructions");
                    const arrow = document.getElementById("arrow");

                    const isOpen = box.style.display === "block";

                    box.style.display = isOpen ? "none" : "block";
                    arrow.style.transform = isOpen ? "rotate(0deg)" : "rotate(180deg)";
                }



                // 🎥 Access camera + microphone
                navigator.mediaDevices.getUserMedia({ video: true, audio: true })
                    .then(s => {
                        stream = s;
                        liveVideo.srcObject = stream;

                        mediaRecorder = new MediaRecorder(stream, {
                            mimeType: "video/webm;codecs=vp8,opus"
                        });

                        mediaRecorder.ondataavailable = e => {
                            if (e.data.size > 0) {
                                recordedChunks.push(e.data);
                            }
                        };
                        mediaRecorder.onstop = async () => {
                            const blob = new Blob(recordedChunks, { type: "video/webm" });
                            const videoURL = URL.createObjectURL(blob);

                            // Preview recorded video
                            recordedVideo.src = videoURL;
                            recordedVideo.style.display = "block";
                            liveVideo.style.display = "none";
                            reRecordBtn.style.display = "block";
                            recordingStatus.style.display = "none";
                            startBtn.style.display = "none";
                            stopBtn.style.display = "none";

                            // Enable Next button
                            nextBtn.disabled = false;

                            // Upload to backend
                            const formData = new FormData();
                            formData.append("video", blob, "kycVideo.webm");

                            await fetch("/uploadVideo", {
                                method: "POST",
                                body: formData
                            });

                            recordedChunks = [];
                        };
                    })
                    .catch(err => {
                        alert("Camera or microphone access denied!");
                        console.error(err);
                    });

                // ▶️ Start recording
                function startRecording() {
                    recordedChunks = [];
                    seconds = 0;
                    timerText.innerText = "Time: 0 sec";

                    mediaRecorder.start();
                    recordingStatus.style.display = "block";

                    startBtn.disabled = true;
                    stopBtn.disabled = false;

                    intervalId = setInterval(() => {
                        seconds++;
                        timerText.innerText = "Time: " + seconds + " sec";

                        if (seconds === MAX_VIDEO_DURATION) {
                            stopRecording();
                        }
                    }, 1000);
                }

                // Stop recording
                function stopRecording() {
                    if (mediaRecorder && mediaRecorder.state !== "inactive") {
                        mediaRecorder.stop();
                    }

                    clearInterval(intervalId);
                    stopBtn.disabled = true;
                    startBtn.disabled = false;
                }

                //Re-record
                function reRecord() {
                    recordedChunks = [];
                    seconds = 0;
                    timerText.innerText = "Time: 0 sec";

                    recordedVideo.style.display = "none";
                    liveVideo.style.display = "block";
                    reRecordBtn.style.display = "none";
                    startBtn.style.display = "inline-block";
                    stopBtn.style.display = "inline-block";

                    nextBtn.disabled = true;
                    startBtn.disabled = false;
                    stopBtn.disabled = true;
                }


            </script>
        </body>

        </html>