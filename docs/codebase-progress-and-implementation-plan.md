# Codebase Progress And Implementation Plan

Ngay doc: 2026-05-02

## 1. Pham vi da kiem tra

- Module Swing chinh o root: `src/main/java/com/example/managementadmissionwf`.
- Ban sao Swing o `swing-app` co cau truc gan nhu trung voi root, hien co nhieu file dang bi sua rieng.
- Module web `thymeleaf_web`.
- Tai lieu luong UI/backend trong `docs/flow-context` va `swing-app/docs/flow-context`.
- Kiem chung ky thuat:
  - `mvn -q -DskipTests compile`: pass cho root, `swing-app`, `thymeleaf_web`.
  - `mvn clean test`: pass cho root, `swing-app`, `thymeleaf_web`; moi module hien chi co 1 test `contextLoads`.
  - Maven Wrapper `mvnw.cmd` dang loi `Cannot start maven from wrapper`; da phai dung Maven trong cache local.

## 2. Tong quan tien do

Uoc luong theo code hien tai:

| Nhom | Tien do | Ghi chu |
| --- | ---: | --- |
| Kien truc Swing + Spring Boot | 75% | Layer UI/controller/service/repository/DTO/mapper da ro, compile va context load duoc. |
| CRUD danh muc/candidate/score/user | 65-80% | Nhieu man da dung DB that, pagination, soft delete, Excel import/export. |
| Nghiep vu xet tuyen | 25-35% | Entity co san nhung AdmissionResultService van in-memory mock, chua co thuat toan xet tuyen. |
| Bang quy doi, diem cong, thong ke | 20-45% | Diem cong co service DB trong ScorePanel, nhung man Diem cong rieng van mock; Bang quy doi va Thong ke chua co service that. |
| Thymeleaf web | 15-25% | Compile duoc, nhung UserController/UserService/SecurityConfig dang bi xoa; templates user con link `/users`. |
| Test/QA | 10-15% | Chi co smoke test context; chua co unit/integration test cho nghiep vu. |

## 3. Tinh nang da tuong doi hoan thien

### Auth va User Management

- `AuthServiceImpl` dang xac thuc bang `UserRepository` va `PasswordEncoder`.
- `UserServiceImpl` co list/search/filter role, create/update/delete soft delete.
- Gioi han lon:
  - Role hien chi co `STUDENT`, `ADMIN`; README co nhac `MANAGER` nhung code chua co.
  - Chua co logout.
  - Dang chan tao/cap nhat ADMIN trong service, nhung form van hien option ADMIN; can dong bo UX va rule.
  - `DataSeeder` dung password `admin123` nhung log lai ghi `password123`.

### Candidate Management

- Da dung `CandidateRepository`, `CandidateMapper`, Bean Validation, paging/search/filter, soft delete cascade sang score/bonus/aspiration.
- Da co import/export Excel bang `ExcelUtil`.
- Can bo sung test duplicate CCCD/SBD/email/phone, import error rows, restore soft-deleted candidate.

### Score Management

- Da dung `ScoreRepository`, lien ket candidate active, CRUD, search, import/export Excel.
- Co Bonus Score thao tac tu nut trong `ScorePanel`.
- Can bo sung validation theo tung phuong thuc THPT/DGNL/VSAT va test tinh diem ngoai ngu/chung chi.

### Major va Subject Group

- `MajorServiceImpl` va `SubjectGroupServiceImpl` da dung DB, soft delete, import/export Excel.
- Major co quan he nganh-to-hop va refresh thong ke sl_*.
- Diem yeu: thong ke major dang dua vao `AdmissionResultService` mock, nen so lieu sl_* chua phan anh DB that.

## 4. Tinh nang chua hoan thien / rui ro chinh

### 4.1. Xet tuyen va nguyenvong

Hien trang:
- Co entity `XtNguyenvongxettuyen`, nhung khong co repository/service that.
- `AdmissionResultServiceImpl` luu `List<AdmissionResultDTO>` in-memory va seed 3 dong mau.
- Export Excel/PDF chi `System.out.println`, chua ghi file.
- Chua co CRUD nguyen vong, chua co tinh diem xet tuyen, chua co phan bo chi tieu.

Ke hoach:
1. Tao `AdmissionPreferenceRepository` cho `XtNguyenvongxettuyen`.
2. Tao DTO request/response cho nguyen vong va ket qua xet tuyen.
3. Tao `AdmissionPreferenceService`: CRUD nguyen vong, rang buoc CCCD ton tai, ma nganh ton tai, thu tu nguyen vong khong trung trong cung thi sinh.
4. Tao `AdmissionScoreCalculator`:
   - Lay diem thi theo CCCD.
   - Lay to hop mon cua nganh.
   - Tinh `diemThxt` theo he so mon.
   - Ap dung bang quy doi neu phuong thuc can quy doi.
   - Cong `diemUtqd` va `diemCong`.
5. Tao `AdmissionProcessingService`:
   - Reset ket qua ve `CHO_XET`.
   - Sap xep theo diem xet tuyen, uu tien nguyen vong, phuong thuc, chi tieu.
   - Dam bao moi thi sinh chi trung tuyen mot nguyen vong cao nhat.
   - Cap nhat `nvKetqua`, `diemThxt`, `diemCong`, `diemXettuyen`.
6. Sua `AdmissionResultServiceImpl` sang DB query thay vi mock.
7. Sua `AdmissionPanel`:
   - Them nut "Chay xet tuyen".
   - Filter theo ket qua, nganh, phuong thuc lay tu DB.
   - Hien thong ke tu ket qua DB.
8. Test:
   - Unit test calculator.
   - Integration test ranking/chi tieu/tie-break.
   - Test mot thi sinh nhieu nguyen vong chi trung mot nguyen vong.

### 4.2. Bang quy doi diem

Hien trang:
- Co entity `XtBangquydoi`.
- Chua co repository/service/DTO/mapper.
- `ConversionTablePanel` dung mock data va TODO cho import/export.

Ke hoach:
1. Tao `ConversionRuleRepository` cho `XtBangquydoi`.
2. Tao DTO `ConversionRuleDTO` va mapper.
3. Tao service CRUD/search/filter theo phuong thuc, to hop, mon.
4. Them import/export Excel cho bang quy doi.
5. Them ham `convertScore(phuongThuc, toHop, mon, rawScore)`:
   - Tim khoang `dDiema <= raw <= dDiemb`.
   - Neu can noi suy, dinh nghia cong thuc ro.
   - Neu khong co rule, tra loi loi nghiep vu co thong diep.
6. Wire `ConversionTablePanel` vao service DB.
7. Tich hop calculator xet tuyen su dung service quy doi.
8. Test khoang diem bien, trung khoang, thieu rule.

### 4.3. Diem cong

Hien trang:
- `BonusScoreServiceImpl` da co DB CRUD.
- `ScorePanel` co nut `BONUS_SCORE` va dialog de them/sua diem cong cho thi sinh da chon.
- `BonusScorePanel` rieng tren menu van mock data va TODO.

Ke hoach:
1. Thay `BonusScorePanel` mock bang controller + `BonusScoreService`.
2. Them search theo CCCD/ho ten, paging server-side.
3. Them CRUD truc tiep tu man Diem cong.
4. Them import/export Excel cho `BonusScoreDTO`.
5. Kiem tra candidate active truoc create/update.
6. Dong bo `diemTong` tinh tu entity/service, khong cho user nhap sai tong.
7. Test duplicate CCCD, soft delete/restore, invalid score range.

### 4.4. Thong ke va bao cao

Hien trang:
- `StatisticPanel` chi la placeholder.
- Chua co service thong ke.
- Major co sl_* nhung du lieu phu thuoc AdmissionResult mock.

Ke hoach:
1. Tao `StatisticsService`.
2. Query cac chi so:
   - Tong thi sinh, tong diem thi, tong nguyen vong.
   - So trung tuyen/truot/cho xet.
   - Ty le trung tuyen theo nganh/phuong thuc/khu vuc/doi tuong.
   - Pho diem theo mon va theo nganh.
3. Tao DTO thong ke rieng cho cards/table/chart.
4. Xay `StatisticPanel` that:
   - Bo loc nam/ky tuyen sinh, nganh, phuong thuc.
   - Cards tong quan.
   - Bang top nganh, pho diem.
   - Export Excel/PDF/Print.
5. Them cache nhe cho query tong hop neu du lieu lon.
6. Test aggregate query voi dataset mau.

### 4.5. User/Auth/RBAC

Hien trang:
- Auth dang la desktop session trong singleton service, khong phai security context.
- Role code chi co `STUDENT`, `ADMIN`; README co `MANAGER`.
- Menu chi an Admission/Statistic neu ADMIN, cac action khac chua co guard day du.
- Signup/Forgot password dang TODO.

Ke hoach:
1. Chot role model: `ADMIN`, `MANAGER`, `STUDENT`.
2. Cap nhat enum, seed data, form, filter, service validation.
3. Tao ma tran permission theo feature/action.
4. Them check permission o UI controller truoc thao tac create/update/delete/import/export.
5. Them logout: clear current user, dong `MainFrame`, mo lai `LoginFrame`.
6. Xu ly forgot password/signup: neu khong can, an link; neu can, them flow that.
7. Test authorization theo role.

### 4.6. Thymeleaf web

Hien trang:
- Module compile va context test pass.
- `UserController`, `UserService`, `SecurityConfig` dang bi xoa.
- Templates van link `/users`, `/users/new`, `/users/{id}`.
- App properties dat datasource read-only nhung form user van tao/sua/xoa.

Ke hoach theo 2 huong:

Huong A - Giu web la ung dung read-only:
1. Xoa/an cac form tao/sua/xoa trong templates.
2. Them controller read-only cho dashboard/list/detail.
3. Dung chung DB schema voi Swing nhung chi query.
4. Them test MockMvc cho `/`, `/users`, `/users/{id}`.

Huong B - Phat trien web thanh ban thay the Swing:
1. Restore/tao lai `UserController`, `UserService`, `SecurityConfig`.
2. Bo read-only datasource.
3. Tao cac module web theo thu tu: auth/user, candidate, score, major, subject group, conversion, admission, statistics.
4. Tach service domain dung chung voi Swing thanh module/shared package neu muon tranh duplicate.
5. Them security + CSRF + role-based pages.

Khuyen nghi: chon mot huong ro. Neu web chua phai muc tieu gan, de `thymeleaf_web` read-only hoac tach khoi luong build chinh.

### 4.7. Test, build, va chat luong

Hien trang:
- Test hien chi la `contextLoads`.
- Tests dung MySQL local that, khong co test profile/doc seed rieng.
- Maven Wrapper loi khi chay `mvnw.cmd`.
- Co canh bao Lombok `@Builder` bo qua default field.

Ke hoach:
1. Sua Maven Wrapper hoac document dung Maven local.
2. Tao `application-test.properties` voi H2/Testcontainers.
3. Them unit tests cho service: User, Candidate, Score, Major, SubjectGroup, BonusScore.
4. Them integration tests repository/service voi DB test.
5. Them test import/export Excel bang file tam.
6. Them test admission algorithm sau khi implement.
7. Sua `@Builder.Default` cho cac field default quan trong.
8. Giam log SQL DEBUG mac dinh, dua vao profile dev.

### 4.8. Repo hygiene / source of truth

Hien trang:
- Root va `swing-app` la hai ban Swing trung lap.
- `swing-app` co 14 file dang modified; `thymeleaf_web` co file deleted/modified.
- Khong co parent Maven multi-module nen build root khong bao gom submodules.

Ke hoach:
1. Chot module chinh: root hay `swing-app`.
2. Neu can ca hai, tao parent `pom.xml` multi-module va dinh nghia module ro.
3. Neu chi can mot, archive/xoa ban duplicate sau khi so diff.
4. Chuan hoa docs ve duong dan source of truth.
5. Them CI script build/test dung dung module.

## 5. Thu tu uu tien de trien khai

1. Repo hygiene + test profile: tranh sua hai codebase song song va tranh test phu thuoc MySQL local.
2. Hoan thien bang quy doi: day la dependency cho tinh diem xet tuyen.
3. Hoan thien diem cong standalone + validation: la input truc tiep cua diem xet tuyen.
4. CRUD nguyen vong + admission repository/service DB.
5. Thuat toan xet tuyen + UI chay xet tuyen.
6. Export PDF/Excel that cho admission results.
7. Statistics/report.
8. RBAC hoan chinh va logout.
9. Quyet dinh va don dep `thymeleaf_web`.

## 6. Acceptance criteria tong quat

- Khong con mock data trong cac man nghiep vu chinh.
- Moi feature CRUD co service/repository/mapper/DTO/test.
- Import/export Excel co test voi file mau.
- Admission processing co test dataset va ket qua xet tuyen lap lai duoc.
- Test chay duoc tren moi may khong can MySQL local san.
- UI khong hien action ma role hien tai khong duoc phep dung.
