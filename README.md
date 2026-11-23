# KasirNota Pro — Android

Proyek Android WebView yang membungkus aplikasi `index.html` asli KasirNota Pro. Fungsi inti dipertahankan: Mode Laundry, Persewaan Barang/Stroller, profil usaha, katalog, kalkulasi nota, pratinjau, riwayat, WhatsApp, cetak, serta backup/import JSON.

## Fitur Android yang ditambahkan
- Menu aplikasi yang dapat diubah dari HP: nama, ikon, tampil/sembunyi, dan urutan.
- Data konfigurasi, menu, dan riwayat disimpan lokal di perangkat melalui WebView localStorage.
- Pemilih file Android untuk Import JSON.
- Ekspor backup JSON ke penyimpanan khusus aplikasi.
- Cetak nota melalui layanan cetak Android.
- Tautan WhatsApp dibuka melalui aplikasi yang sesuai.

## Membuat APK langsung dari HP menggunakan GitHub
1. Ekstrak ZIP proyek ini di HP.
2. Buat repository baru di GitHub, misalnya `KasirNotaPro-Android`.
3. Unggah seluruh isi folder proyek (bukan folder ZIP-nya). Pastikan folder `.github/workflows` ikut terunggah. Jika aplikasi GitHub menyembunyikan file/folder titik, gunakan GitHub lewat browser dengan tampilan Desktop atau unggah file workflow secara terpisah.
4. Setelah file masuk, buka tab **Actions** pada repository.
5. Pilih workflow **Build KasirNota Pro APK** lalu tekan **Run workflow**. Jika workflow otomatis berjalan setelah commit, tunggu sampai selesai.
6. Buka hasil workflow yang berstatus hijau, cari bagian **Artifacts**, lalu unduh `KasirNota-Pro-APK`.
7. Ekstrak ZIP artifact dan instal `app-debug.apk` di Android. Jika diminta, izinkan instalasi dari sumber tersebut.

## Catatan penting
- Proyek ini adalah sumber aplikasi dan workflow pembuat APK, bukan APK yang sudah dikompilasi.
- `index.html` asli menggunakan Tailwind CDN dan Google Fonts, sehingga koneksi internet diperlukan untuk memuat gaya tampilan eksternal. Logika kasir dan penyimpanan lokal tetap berada di aplikasi, tetapi tampilan paling lengkap saat internet tersedia.
- Data disimpan di perangkat ini saja. Hapus data aplikasi atau uninstall dapat menghapus data lokal. Gunakan Backup JSON secara berkala.
- Ekspor backup disimpan di direktori file khusus aplikasi Android. Untuk memindahkannya, gunakan pengelola file Android yang dapat membuka `Android/data/id.kasirnota.pro/files`.
- Cetak membutuhkan layanan cetak Android/printer yang kompatibel. Thermal Bluetooth mungkin memerlukan aplikasi/driver printer tersendiri.
- Uji dahulu dengan transaksi percobaan sebelum dipakai untuk transaksi usaha sebenarnya.
