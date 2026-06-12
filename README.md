# 💪 FitGrid
> Aplikasi panduan workout & kebugaran Android yang membantu pengguna menemukan latihan, menghitung BMI, dan mencatat progres olahraga harian.

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Java](https://img.shields.io/badge/Language-Java-orange?logo=java)
![API](https://img.shields.io/badge/API-ExerciseDB%20RapidAPI-blue)
![Version](https://img.shields.io/badge/Version-1.0-brightgreen)

---

## 📱 Tampilan Aplikasi

| Home | Exercise List | Detail |
|------|--------------|--------|
| Beranda dengan search, filter kategori, BMI Calc & Workout Log | Daftar exercise per kategori otot | Detail gerakan + instruksi + bookmark |

| Saved | Workout Log | Settings |
|-------|-------------|----------|
| Daftar exercise tersimpan | Catat sesi latihan harian | Toggle Dark/Light Mode |

---

## ✨ Fitur Utama

- 🔍 **Pencarian & Filter** — Cari exercise berdasarkan nama, filter berdasarkan kategori otot (Back, Cardio, Chest, dll)
- 📋 **Detail Exercise** — Lihat instruksi lengkap, otot yang dilatih, dan peralatan yang dibutuhkan
- 🔖 **Simpan Exercise** — Bookmark exercise favorit ke penyimpanan lokal
- 📊 **BMI Calculator** — Hitung Body Mass Index berdasarkan berat dan tinggi badan
- 📝 **Workout Log** — Catat sesi latihan (exercise, sets, reps, catatan) per tanggal
- 🌙 **Dark & Light Mode** — Dukungan tema gelap dan terang
- 📶 **Mode Offline** — Exercise yang disimpan tetap dapat diakses tanpa koneksi internet
- 🔄 **Retry saat offline** — Banner error + tombol retry saat koneksi gagal

---

## 🧱 Spesifikasi Teknis

| Komponen | Implementasi |
|----------|-------------|
| **Activity (≥2)** | `MainActivity` (launcher), `BmiActivity`, `DetailActivity`, `WorkoutLogActivity` |
| **Intent** | Perpindahan antar Activity (Home → Detail, Home → BMI, Detail → WorkoutLog) |
| **Fragment + Navigation** | `HomeFragment`, `SavedFragment`, `SettingsFragment` dengan Navigation Component + BottomNavigationView |
| **RecyclerView** | `ExerciseAdapter` (grid exercise), `WorkoutLogAdapter` (log list), `CategoryAdapter` (chip filter) |
| **Background Thread** | `AppExecutor` (Executor + Handler) untuk operasi Retrofit & database |
| **Networking** | Retrofit + penanganan kegagalan: banner offline + tombol Retry |
| **Penyimpanan Lokal** | SQLite via `DatabaseHelper` untuk saved exercise & workout log |
| **SharedPreferences** | `SharedPrefManager` untuk menyimpan preferensi tema |
| **Tema Gelap/Terang** | `AppCompatDelegate.setDefaultNightMode` + `res/values/themes.xml` (2 tema) |

---

## 🔌 API

**ExerciseDB API** via RapidAPI — [https://rapidapi.com/justin-WFnsXH_t6/api/exercisedb](https://rapidapi.com/justin-WFnsXH_t6/api/exercisedb)

| Endpoint | Kegunaan |
|----------|----------|
| `GET /exercises` | Mengambil semua daftar exercise |
| `GET /exercises/bodyPart/{bodyPart}` | Filter exercise berdasarkan kategori otot |
| `GET /exercises/exercise/{id}` | Mengambil detail exercise berdasarkan ID |
| `GET /exercises/bodyPartList` | Mengambil daftar kategori otot |

**Headers yang dibutuhkan:**
```
x-rapidapi-host: exercisedb.p.rapidapi.com
x-rapidapi-key: YOUR_RAPIDAPI_KEY
```

> ⚠️ API Key diperlukan. Daftarkan akun di [rapidapi.com](https://rapidapi.com) dan subscribe ke ExerciseDB untuk mendapatkan key gratis.

---

## 📂 Struktur Project

```
com.example.fitgrid
├── activity/
│   ├── MainActivity.java          # Launcher + bottom navigation
│   ├── BmiActivity.java           # Kalkulator BMI
│   ├── DetailActivity.java        # Detail exercise
│   └── WorkoutLogActivity.java    # Catat sesi latihan
├── adapter/
│   ├── CategoryAdapter.java       # Chip filter kategori
│   ├── ExerciseAdapter.java       # Grid daftar exercise
│   └── WorkoutLogAdapter.java     # List workout log
├── api/
│   ├── ApiService.java            # Interface endpoint Retrofit
│   └── RetrofitInstance.java      # Konfigurasi Retrofit client
├── database/
│   └── DatabaseHelper.java        # SQLite: saved & workout log
├── fragment/
│   ├── HomeFragment.java          # Beranda + search + filter
│   ├── SavedFragment.java         # Daftar exercise tersimpan
│   └── SettingsFragment.java      # Pengaturan tema
├── listener/
│   └── OnItemClickListener.java
├── model/
│   ├── ExerciseItem.java
│   └── WorkoutLog.java
└── utils/
    ├── AppExecutor.java           # Executor + Handler
    ├── NetworkUtil.java           # Cek koneksi internet
    └── SharedPrefManager.java     # Preferensi tema

res/
├── layout/
│   ├── activity_main.xml
│   ├── activity_bmi.xml
│   ├── activity_detail.xml
│   ├── activity_workout_log.xml
│   ├── fragment_home.xml
│   ├── fragment_saved.xml
│   ├── fragment_settings.xml
│   ├── item_category.xml
│   ├── item_exercise.xml
│   └── item_workout_log.xml
├── menu/
│   └── bottom_nav_menu.xml
├── navigation/
│   └── nav_graph.xml
├── values/
│   └── themes (2) + colors.xml + strings.xml
└── xml/
    └── network_security_config.xml
```

---

## ⚙️ Cara Install

### Cara 1 — Via APK

1. Buka halaman **Releases** di GitHub repository ini
2. Download file `app-debug.apk`
3. Pindahkan ke perangkat Android
4. Aktifkan **Install from unknown sources**: Pengaturan → Keamanan → Install from unknown sources → ON
5. Buka file APK → Install → Buka aplikasi **FitGrid**

### Cara 2 — Build dari Source Code

**Persyaratan:**
- Android Studio (versi terbaru)
- Java JDK 11 atau lebih tinggi
- Koneksi internet
- RapidAPI Key (ExerciseDB)

**Langkah-langkah:**

```bash
# 1. Clone repository
git clone https://github.com/<username>/FitGrid.git

# 2. Buka di Android Studio
# File → Open → pilih folder FitGrid

# 3. Tambahkan API Key di local.properties
RAPIDAPI_KEY=your_rapidapi_key_here

# 4. Tunggu Gradle sync selesai

# 5. Jalankan ke perangkat/emulator
# Klik tombol Run (Shift+F10)
```

> 🔑 Dapatkan API Key gratis di [rapidapi.com](https://rapidapi.com) → search "ExerciseDB" → Subscribe ke plan Free.

---

## 📲 Cara Penggunaan

1. **Buka aplikasi** — Halaman Home menampilkan daftar exercise
2. **Cari exercise** — Ketik nama di kolom pencarian atau pilih kategori otot
3. **Lihat detail** — Tap exercise card untuk melihat instruksi lengkap
4. **Simpan exercise** — Tap ikon bookmark (🔖) di halaman detail
5. **Hitung BMI** — Tap tombol **BMI Calc** di beranda, masukkan berat & tinggi
6. **Catat latihan** — Tap **Workout Log** atau **+ Log This Workout** di detail exercise
7. **Mode offline** — Exercise tersimpan tetap dapat diakses tanpa internet
8. **Ganti tema** — Buka tab **Settings** → toggle Dark/Light Mode

---

## 🛠️ Teknologi yang Digunakan

| Teknologi | Kegunaan |
|-----------|----------|
| Java | Bahasa pemrograman utama |
| Retrofit 2 | HTTP client untuk konsumsi API |
| Glide | Loading gambar exercise |
| SQLite | Penyimpanan lokal (saved & log) |
| SharedPreferences | Preferensi tema |
| RecyclerView | Menampilkan daftar exercise & log |
| Navigation Component | Navigasi antar Fragment |
| Material Design 3 | Komponen UI modern |
| Executor + Handler | Background thread operations |

---

## 👤 Developer

| | |
|--|--|
| **Nama** | David |
| **Tema** | Kesehatan & Kebugaran |
| **API** | ExerciseDB (RapidAPI) |
| **Tahun** | 2026 |

---

## 📄 Lisensi

Project ini dibuat untuk keperluan **Tugas Final Lab Mobile Programming 2026**.
