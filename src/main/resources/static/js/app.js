// Smart Campus JavaScript Utilities

// Quick Demo Login Auto-fill for Examiners / Evaluators
function fillLogin(username, password) {
    const userField = document.getElementById('username');
    const passField = document.getElementById('password');
    if (userField && passField) {
        userField.value = username;
        passField.value = password;
        userField.focus();
    }
}

// Live Marks Calculator for Lecturer Marks Entry Sheet
function updateGradeRow(regId) {
    const cwInput = document.getElementById('cw_' + regId);
    const examInput = document.getElementById('exam_' + regId);
    const totalSpan = document.getElementById('total_' + regId);
    const gradeSpan = document.getElementById('grade_' + regId);
    const gpSpan = document.getElementById('gp_' + regId);

    if (!cwInput || !examInput) return;

    let cw = parseFloat(cwInput.value);
    let exam = parseFloat(examInput.value);

    if (isNaN(cw)) cw = 0;
    if (isNaN(exam)) exam = 0;

    // Enforce ranges
    if (cw > 40) { cw = 40; cwInput.value = 40; }
    if (cw < 0) { cw = 0; cwInput.value = 0; }
    if (exam > 60) { exam = 60; examInput.value = 60; }
    if (exam < 0) { exam = 0; examInput.value = 0; }

    const total = Math.round((cw + exam) * 10) / 10;
    if (totalSpan) totalSpan.innerText = total.toFixed(1);

    let letter = 'F';
    let gp = 0.0;

    if (total >= 80) {
        letter = 'A';
        gp = 5.0;
    } else if (total >= 70) {
        letter = 'B';
        gp = 4.0;
    } else if (total >= 60) {
        letter = 'C';
        gp = 3.0;
    } else if (total >= 50) {
        letter = 'D';
        gp = 2.0;
    } else {
        letter = 'F';
        gp = 0.0;
    }

    if (gradeSpan) {
        gradeSpan.innerText = letter;
        gradeSpan.className = 'badge ' + (total >= 50 ? 'bg-success' : 'bg-danger');
    }
    if (gpSpan) {
        gpSpan.innerText = gp.toFixed(1);
    }
}

// Print Transcript trigger
function printTranscript() {
    window.print();
}

// Client-side Password Generator for Form Inputs
function generateRandomPassword(inputId) {
    const uppers = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    const lowers = "abcdefghjkmnpqrstuvwxyz";
    const digits = "23456789";
    const specials = "!@#$%&*";
    const all = uppers + lowers + digits + specials;

    let pwd = "";
    pwd += uppers.charAt(Math.floor(Math.random() * uppers.length));
    pwd += lowers.charAt(Math.floor(Math.random() * lowers.length));
    pwd += digits.charAt(Math.floor(Math.random() * digits.length));
    pwd += specials.charAt(Math.floor(Math.random() * specials.length));

    for (let i = 4; i < 9; i++) {
        pwd += all.charAt(Math.floor(Math.random() * all.length));
    }

    // Shuffle
    pwd = pwd.split('').sort(() => 0.5 - Math.random()).join('');

    const input = document.getElementById(inputId);
    if (input) {
        input.value = pwd;
        input.type = 'text'; // temporarily show generated password
        input.focus();
    }
}
