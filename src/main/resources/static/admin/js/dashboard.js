const token = localStorage.getItem("adminToken");

fetch("http://localhost:8080/api/admin/protected-endpoint", {
    method: "GET",
    headers: {
        "Authorization": `Bearer ${token}`
    }
})
.then(res => res.json())
.then(data => {
    console.log("Protected data:", data);
})
.catch(err => {
    console.error("Unauthorized or error:", err);
});
