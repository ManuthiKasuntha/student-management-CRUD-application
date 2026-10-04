// The backend URL. Because this page is served by Spring Boot itself,
// a relative path is enough (same as http://localhost:8080/api/students).
const API_URL = "/api/students";

// ---------- Get references to HTML elements ----------
const form = document.getElementById("student-form");
const formTitle = document.getElementById("form-title");
const idInput = document.getElementById("student-id");
const saveBtn = document.getElementById("save-btn");
const cancelBtn = document.getElementById("cancel-btn");
const message = document.getElementById("message");
const table = document.getElementById("student-table");
const emptyText = document.getElementById("empty-text");

const searchInput = document.getElementById("search");
const courseFilter = document.getElementById("filter-course");
const statusFilter = document.getElementById("filter-status");
const pageSizeSelect = document.getElementById("page-size");
const prevBtn = document.getElementById("prev-btn");
const nextBtn = document.getElementById("next-btn");
const pageInfo = document.getElementById("page-info");

// The form fields, by the same names the Student class uses
const fields = ["name", "email", "age", "phone", "course", "status", "enrollmentDate"];
const input = {};
fields.forEach(f => input[f] = document.getElementById(f));

// ---------- Current state of the list ----------
const state = {
    page: 0,             // Spring pages start at 0
    totalPages: 1,
    sortField: "id",
    sortDir: "asc"
};

// ================== READ: load one page of students ==================
async function loadStudents() {
    // Build the URL, e.g. /api/students?page=0&size=5&sort=name,asc&keyword=ka
    const params = new URLSearchParams({
        page: state.page,
        size: pageSizeSelect.value,
        sort: `${state.sortField},${state.sortDir}`
    });
    if (searchInput.value.trim()) params.append("keyword", searchInput.value.trim());
    if (courseFilter.value) params.append("course", courseFilter.value);
    if (statusFilter.value) params.append("status", statusFilter.value);

    const response = await fetch(`${API_URL}?${params}`);
    const data = await response.json();   // { content: [...], page, size, totalElements, totalPages }

    // If we deleted the last student on the last page, go back one page
    if (data.content.length === 0 && state.page > 0) {
        state.page--;
        return loadStudents();
    }

    state.totalPages = Math.max(data.totalPages, 1);
    renderTable(data.content);
    pageInfo.textContent = `Page ${data.page + 1} of ${state.totalPages} (${data.totalElements} students)`;
    prevBtn.disabled = state.page === 0;
    nextBtn.disabled = state.page >= state.totalPages - 1;
    updateSortArrows();
}

function renderTable(students) {
    table.innerHTML = "";
    emptyText.hidden = students.length > 0;

    for (const s of students) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${s.id}</td>
            <td>${escapeHtml(s.name)}</td>
            <td>${escapeHtml(s.email)}</td>
            <td>${s.age ?? ""}</td>
            <td>${escapeHtml(s.course)}</td>
            <td>${s.enrollmentDate ?? ""}</td>
            <td>
                <select class="status-select ${s.status}">
                    <option value="ACTIVE">Active</option>
                    <option value="GRADUATED">Graduated</option>
                    <option value="SUSPENDED">Suspended</option>
                </select>
            </td>
            <td>
                <button class="edit">Edit</button>
                <button class="delete">Delete</button>
            </td>`;

        const statusSelect = row.querySelector(".status-select");
        statusSelect.value = s.status;
        statusSelect.addEventListener("change", () => changeStatus(s.id, statusSelect.value));
        row.querySelector(".edit").addEventListener("click", () => startEdit(s));
        row.querySelector(".delete").addEventListener("click", () => deleteStudent(s));
        table.appendChild(row);
    }
}

// ================== CREATE or UPDATE: when the form is submitted ==================
form.addEventListener("submit", async (event) => {
    event.preventDefault();               // stop the page from reloading
    clearFieldErrors();

    const student = {
        name: input.name.value,
        email: input.email.value,
        age: input.age.value === "" ? null : Number(input.age.value),
        phone: input.phone.value || null,
        course: input.course.value,
        status: input.status.value,
        enrollmentDate: input.enrollmentDate.value || null
    };

    const id = idInput.value;
    const isUpdate = id !== "";

    const response = await fetch(isUpdate ? `${API_URL}/${id}` : API_URL, {
        method: isUpdate ? "PUT" : "POST",    // PUT = update, POST = create
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(student)         // JavaScript object -> JSON text
    });

    if (response.ok) {
        showMessage(isUpdate ? "Student updated!" : "Student added!", "success");
        resetForm();
        refreshAll();
    } else {
        // Our GlobalExceptionHandler always sends { message, errors: { field: text } }
        const error = await response.json();
        showMessage(error.message, "error");
        showFieldErrors(error.errors);
    }
});

// ================== PATCH: change only the status ==================
async function changeStatus(id, status) {
    const response = await fetch(`${API_URL}/${id}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status })
    });

    if (response.ok) {
        showMessage(`Student ${id} is now ${status}.`, "success");
    } else {
        showMessage((await response.json()).message, "error");
    }
    refreshAll();
}

// ================== DELETE ==================
async function deleteStudent(student) {
    if (!confirm(`Delete ${student.name}?`)) return;

    const response = await fetch(`${API_URL}/${student.id}`, { method: "DELETE" });

    if (response.ok) {
        showMessage(`${student.name} deleted.`, "success");
        if (idInput.value === String(student.id)) resetForm();
        refreshAll();
    } else {
        showMessage((await response.json()).message, "error");
    }
}

// ================== Statistics and course lists ==================
async function loadStats() {
    const stats = await (await fetch(`${API_URL}/stats`)).json();

    document.getElementById("stat-total").textContent = stats.total;
    document.getElementById("stat-active").textContent = stats.active;
    document.getElementById("stat-graduated").textContent = stats.graduated;
    document.getElementById("stat-suspended").textContent = stats.suspended;
    document.getElementById("stat-age").textContent = stats.averageAge;

    // Bar chart: the biggest course gets a full-width bar
    const chart = document.getElementById("course-chart");
    const max = Math.max(1, ...stats.byCourse.map(c => c.count));
    chart.innerHTML = stats.byCourse.length === 0 ? "<p>No data yet.</p>" : "";
    for (const c of stats.byCourse) {
        const row = document.createElement("div");
        row.className = "bar-row";
        row.innerHTML = `
            <span>${escapeHtml(c.course)}</span>
            <div class="bar" style="width: ${(c.count / max) * 100}%"></div>
            <strong>${c.count}</strong>`;
        chart.appendChild(row);
    }
}

async function loadCourses() {
    const courses = await (await fetch(`${API_URL}/courses`)).json();

    // Fill the filter dropdown, keeping the current choice
    const selected = courseFilter.value;
    courseFilter.innerHTML = `<option value="">All courses</option>`;
    for (const c of courses) {
        courseFilter.add(new Option(c, c));
    }
    courseFilter.value = courses.includes(selected) ? selected : "";

    // Fill the suggestions for the Course field in the form
    document.getElementById("course-options").innerHTML =
        courses.map(c => `<option value="${escapeHtml(c)}">`).join("");
}

// After any change, reload everything that could be affected
function refreshAll() {
    loadStudents();
    loadStats();
    loadCourses();
}

// ================== Search, filter, sort, pagination events ==================

// Wait until the user stops typing for 300 ms, then search
let searchTimer;
searchInput.addEventListener("input", () => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => { state.page = 0; loadStudents(); }, 300);
});

[courseFilter, statusFilter, pageSizeSelect].forEach(el =>
    el.addEventListener("change", () => { state.page = 0; loadStudents(); }));

prevBtn.addEventListener("click", () => { state.page--; loadStudents(); });
nextBtn.addEventListener("click", () => { state.page++; loadStudents(); });

// Click a column header to sort; click again to reverse the order
document.querySelectorAll("th[data-sort]").forEach(th => {
    th.addEventListener("click", () => {
        const field = th.dataset.sort;
        if (state.sortField === field) {
            state.sortDir = state.sortDir === "asc" ? "desc" : "asc";
        } else {
            state.sortField = field;
            state.sortDir = "asc";
        }
        state.page = 0;
        loadStudents();
    });
});

function updateSortArrows() {
    document.querySelectorAll("th[data-sort]").forEach(th => {
        const label = th.textContent.replace(/ [▲▼]$/, "");
        const arrow = th.dataset.sort === state.sortField ? (state.sortDir === "asc" ? " ▲" : " ▼") : "";
        th.textContent = label + arrow;
    });
}

// ================== Form helpers ==================

// Fill the form with a student's data so the user can edit it
function startEdit(student) {
    clearFieldErrors();
    idInput.value = student.id;
    fields.forEach(f => input[f].value = student[f] ?? "");
    formTitle.textContent = `Edit Student #${student.id}`;
    saveBtn.textContent = "Update";
    cancelBtn.hidden = false;
    message.textContent = "";
    form.scrollIntoView({ behavior: "smooth" });
}

function resetForm() {
    form.reset();
    idInput.value = "";
    clearFieldErrors();
    formTitle.textContent = "Add Student";
    saveBtn.textContent = "Add";
    cancelBtn.hidden = true;
}

cancelBtn.addEventListener("click", () => {
    resetForm();
    message.textContent = "";
});

// Show each server validation message under its input
function showFieldErrors(errors) {
    for (const [field, text] of Object.entries(errors || {})) {
        const small = document.querySelector(`.field-error[data-for="${field}"]`);
        if (small) small.textContent = text;
        if (input[field]) input[field].classList.add("invalid");
    }
}

function clearFieldErrors() {
    document.querySelectorAll(".field-error").forEach(el => el.textContent = "");
    fields.forEach(f => input[f].classList.remove("invalid"));
}

function showMessage(text, type) {
    message.textContent = text;
    message.className = type;             // "success" or "error" (see style.css)
}

// Prevent HTML in names/emails from being run as code
function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text ?? "";
    return div.innerHTML;
}

// ================== Start ==================
refreshAll();
