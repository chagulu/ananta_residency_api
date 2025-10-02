document.getElementById('adminLoginForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const data = {
        username: document.getElementById('username').value.trim(),
        password: document.getElementById('password').value
    };

    fetch('http://localhost:8080/api/admin/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (!res.ok) throw new Error("Invalid credentials");
        return res.json();
    })
    .then(response => {
        if (response.success && response.data.token) {
            // Save token & username in localStorage
            localStorage.setItem("adminToken", response.data.token);
            localStorage.setItem("adminUsername", response.data.username); // optional

            // Redirect to dashboard
            window.location.href = "/admin/dashboard";
        } else {
            throw new Error("Login failed");
        }
    })
    .catch(err => {
        document.getElementById('error-msg').innerText = err.message;
    });
});
