# ТЗ — Приложение ReadTracker (Kotlin)
## Полное описание UX/UI и функциональности

---

## 1. ОБЩИЕ ПРИНЦИПЫ ДИЗАЙНА

### Шрифт
Во всём приложении используется шрифт **Plus Jakarta Sans** (Google Fonts). Веса: 500 (обычный), 600 (полужирный), 700 (жирный), 800 и 900 (очень жирный). Замена недопустима — именно этот шрифт обязателен.

### Цветовая палитра

| Назначение | HEX | Описание |
|---|---|---|
| **Акцент (основной)** | `#FF9F0A` | Тёплый янтарно-оранжевый, главный цвет бренда |
| **Акцент приглушённый** | `#FF9F0A` с 12% прозрачностью | Фон иконок, бейджей |
| **Статус: В планах** | `#60A5FA` | Голубой |
| **Статус: Читаю** | `#34D399` | Зелёный |
| **Статус: На паузе** | `#FBBF24` | Жёлтый |
| **Статус: Завершено** | `#A78BFA` | Фиолетовый |
| **Статус: Брошено** | `#F87171` | Красный |

### Темы оформления
Приложение поддерживает **три темы**, переключаемые в настройках:

1. **AMOLED** — фон `#000000` (чистый чёрный), карточки `#141414`
2. **Тёмная** — фон `#0F0F0F`, карточки `#1C1C1E`
3. **Светлая** — фон `#F2F2F7`, карточки `#FFFFFF`

При тёмных темах текст белый, при светлой — чёрный. Разделители: в тёмной теме `white/10%`, в светлой `black/12%`.

### Скругления углов
- Малое: **8dp** — кнопки внутри карточек, иконки, бейджи
- Стандартное: **12dp** — карточки тайтлов, поля ввода, группы
- Большое: **20dp** — модальные шторки снизу, диалоги, карточки «Поделиться»

### Отступы
- Малый: **8dp**
- Стандартный: **16dp**
- Большой: **24dp**

### Поля ввода (TextField)
- Заливка: цвет карточки текущей темы
- Без рамки в обычном состоянии
- При фокусе появляется рамка `#FF9F0A` толщиной 1.5dp
- Внутренние отступы: 14dp по горизонтали и вертикали
- Шрифт подсказки: серый, 14sp

### Переключатели (Switch)
- Ручка: белая
- Дорожка при включении: `#FF9F0A`
- Дорожка при выключении: `white/24%` (тёмная) или `black/26%` (светлая)

---

## 2. НАВИГАЦИЯ

### Нижняя панель навигации (BottomNavigationBar)
Показывается по умолчанию. Содержит два раздела:

1. **Библиотека** — иконка `library_books_rounded`
2. **Аналитика** — иконка `bar_chart_rounded`

Оформление нижней панели:
- Фон: цвет карточки текущей темы
- Активный элемент: цвет `#FF9F0A`, жирный шрифт 11sp
- Неактивный: серый, обычный шрифт 11sp
- Тень: отсутствует (elevation 0)
- Над панелью — тонкий разделитель (0.5dp) цвета темы

### Плавающая кнопка добавления (FAB)
- Видна только на вкладке «Библиотека»
- Цвет: `#FF9F0A`, иконка `add_rounded` белого цвета (26dp)
- Форма: прямоугольник со скруглением 16dp (не круглая)
- При нажатии открывает экран «Добавить тайтл»

### Режим скрытой нижней панели
Если в настройках включён параметр «Скрыть нижний бар», нижняя навигация исчезает. В этом режиме:
- Показывается только экран «Библиотека»
- В правой части AppBar'а библиотеки появляется дополнительная иконка-кнопка `bar_chart_rounded` цвета `#FF9F0A`, ведущая на «Аналитику»

---

## 3. ЭКРАН «БИБЛИОТЕКА»

### AppBar
- Высота: 44dp
- Заголовок: **«Библиотека»**, шрифт Plus Jakarta Sans, 22sp, вес 800, без смещения (выровнен по левому краю)
- Кнопки справа (если включены в настройках):
  - Иконка `bar_chart_rounded` цвета `#FF9F0A` — переход на Аналитику (только в режиме скрытой нижней панели)
  - Иконка `ios_share_rounded` цвета `#FF9F0A` — открытие шторки «Поделиться»
  - Каждая кнопка в контейнере 40×40dp, без внутренних отступов
- Фон AppBar: фон текущей темы, тени нет, нет эффекта при скролле

### Вкладки (TabBar)
Расположены под заголовком, высота 36dp:
- Вкладки: **Все / Читаю / В планах / Завершено / На паузе / Брошено**
- Скроллируются горизонтально (isScrollable = true), выравнивание по началу
- Активная вкладка: цвет `#FF9F0A`, жирный шрифт 13sp, снизу подчёркивание `#FF9F0A` толщиной 2dp со скруглёнными концами
- Неактивная: серый, 13sp, вес 500
- Горизонтальный отступ каждой вкладки: 10dp с каждой стороны
- Разделитель под TabBar: прозрачный (не виден)

### Пустой список
Если на вкладке нет тайтлов, в центре экрана показывается заглушка:
- Круглый контейнер с фоном `#FF9F0A/12%`, внутри иконка `menu_book_rounded` (40dp, цвет `#FF9F0A`)
- Ниже текст **«Список пуст»** — 17sp, вес 700
- Ещё ниже **«Нажмите + чтобы добавить тайтл»** — 13sp, серый

### Список тайтлов
Список прокручивается. Отступ сверху 6dp, снизу 100dp (чтобы FAB не перекрывал последний элемент). Существуют два режима отображения карточек — переключаются в настройках.

---

## 4. КАРТОЧКА ТАЙТЛА — РЕЖИМ С ОБЛОЖКОЙ

Контейнер с отступами: горизонтальные 16dp, вертикальные 4dp. Внутренний отступ 10dp. Фон — цвет карточки темы, скругление 12dp.

**Нажатие** → переход на экран редактирования.  
**Долгое нажатие** → диалог подтверждения удаления.

### Внутреннее устройство (горизонтальный ряд):

**1. Обложка (слева)**
- Размер: 52×74dp, скругление 8dp
- Если загружено локальное изображение — показать его (fit: cover)
- Если задан URL — загрузить с кешированием, во время загрузки фон = цвет обложки с 30% прозрачностью, при ошибке — иконка `broken_image_rounded` серая 20dp по центру
- Если обложки нет — фон акцентного цвета книги, иконка `image_rounded` серая 22dp по центру

**2. Основная информация (по центру, растянута)**

Строка 1 — Название:
- Шрифт 14sp, вес 700, максимум 2 строки, обрезание многоточием
- Если включена функция «Оценка» и оценка выставлена: справа от названия (с отступом 4dp) небольшой прямоугольный бейдж с текстом оценки (например: «7/10 ★» или «4/5 ★»), фон `#FF9F0A/12%`, цвет текста `#FF9F0A`, шрифт 10sp вес 800, скругление 6dp, отступы 6dp гор / 2dp верт

Строка 2 — Бейджи статуса и типа (с переносом — Wrap):
- **Кружок статуса**: диаметр 7dp, цвет статуса, рядом текст статуса (11sp, вес 700, цвет статуса)
- **«LN+WN»** — бейдж цвет `#FF9F0A` (показывается если гибридный формат)
- **«Серия»** — бейдж цвет `#A78BFA` (если серия томов, не гибрид)
- **«Веб»** — бейдж цвет `#FBBF24` (если веб-новелла, не гибрид)
- **«Сингл»** — бейдж цвет `#FF9F0A`
- **«Онг.»** — бейдж цвет `#34D399` (если онгоинг)
- **«Старт: т. N»** или **«Старт: гл. N»** — бейдж цвет `#34D399` (только если включена функция «Читать после адаптации» и указан стартовый том/глава)

Оформление бейджа: фон = цвет бейджа с 10% прозрачностью, текст = цвет бейджа, шрифт 10sp вес 700, скругление 6dp, отступы 6dp гор / 2dp верт. Между бейджами: 5dp горизонт., 4dp вертикальный (перенос).

Строка 3 — Прогресс (серый, 11sp):
- Иконка `text_fields_rounded` 11dp серая + «N сл.» (количество слов)
- Если учитываются тома: иконка `layers_rounded` 11dp серая + «N/M т.» или «N/? т.» (онгоинг)
- Если включено отображение глав для веб и тайтл — веб или гибрид: иконка `format_list_numbered_rounded` 11dp серая + «N/M гл.»
- Если включены закладки и позиция закладки = «В ряд»: иконка `bookmark_rounded` 11dp цвет `#FF9F0A` + текст закладки (цвет `#FF9F0A`, 11sp, вес 600, не более 1 строки, обрезание)
- Между блоками: отступ 10dp

Строка 4 (опциональная) — Закладка снизу:
- Показывается только если включены закладки, позиция = «Снизу», и текст закладки не пуст
- Отступ сверху 4dp
- Иконка `bookmark_rounded` 11dp цвет `#FF9F0A` + текст закладки (цвет `#FF9F0A`, 11sp, вес 600)

**3. Стрелка вправо (справа)**
- Иконка `chevron_right_rounded` серая, 18dp

---

## 5. КАРТОЧКА ТАЙТЛА — КОМПАКТНЫЙ РЕЖИМ (без обложки)

Контейнер: отступы горизонтальные 16dp, vertical'nye 3dp. Vnutrennie: gorizontal'nye 12dp, vertical'nye 11dp. Fon — cvet kartochki, skruglenie 12dp.

**Najatie** → redaktirovanie. **Dolgoe najatie** → dialog udaleniya.

### Vnutrennee ustroystvo (gorizontal'nyy ryad):

**1. Poloska statusa (kraynyaya levaya)**
- Razmer: 3×40dp, cvet statusa, skruglenie 2dp

**2. Osnovnaya informaciya (rastyagnuta)**

Stroka 1: Nazvanie (14sp, ves 700, 1 stroka, obrezanie) + sprava beydj ocenki + beydji tipa/formata (takie je, kak v rejime s oblojkoy)

Stroka 2: Seryy progress — slova, toma, glavy (analogichno rejimu s oblojkoy)

Stroka zakladki i startovogo toma — analogichno rejimu s oblojkoy.

---

## 6. DIALOG UDALENIYA TAYTLA

Poyavlyaetsya po dolgomu najatiyu na kartochku.

- Fon: cvet kartochki temy, skruglenie 20dp
- Zagolovok: **«Udalit' taytl?»** — 17sp, ves 800
- Tekst: «`[Nazvanie]` budet udalyon bez vozmojnosti vosstanovleniya.» — 14sp, seryy
- Knopka **«Otmena»** — seryy, ves 600
- Knopka **«Udalit'»** — cvet `#F87171`, ves 700

---

## 7. SHTORKA «PODELIT'SYA» (BottomSheet)

Otkryvaetsya po najatiyu na ikonku `ios_share_rounded` v AppBar biblioteki. Modal'naya shtorka snizu.

- Fon: cvet kartochki, skruglenie 20dp sverhu
- Vnutrennie otstupy: 16dp sleva/sprava/snizu, 12dp sverhu
- Vverhu po centru — indikator-«pilyulya»: 36×4dp, seryy/30%, skruglenie 2dp
- Zagolovok **«Podelit'sya»** — 17sp, ves 800, vyravnivanie po levomu krayu
- Dve plitki opciy (sm. komponent «Plitka deystviya s knopkoy»):

**Plitka 1 — «Analitika»:**
- Ikonka `analytics_outlined` cvet `#FF9F0A`
- Zagolovok «Analitika», podzagolovok «Kartochka so statistikoy»
- Pri najatii: zakryt' shtorku → pereyti na ekran «Podelit'sya — Analitika»

**Plitka 2 — «Spisok taytlov»:**
- Ikonka `format_list_bulleted_rounded` cvet `#34D399`
- Zagolovok «Spisok taytlov», podzagolovok «Vse taytly v odnoy kartochke»
- Pri najatii: zakryt' shtorku → pereyti na ekran «Podelit'sya — Spisok»

### Komponent «Plitka deystviya s knopkoy» (_ShareOptionTile):
- Konteyner s gorizontal'nym ryadom
- Fon: cvet elementa s 8% prozrachnost'yu
- Ramka: cvet elementa s 18% prozrachnost'yu, skruglenie 12dp
- Vnutrennie otstupy: 16dp gorizont., 13dp vert.
- Sleva: ikonka v konteynere (10dp vnutr. otstupy, fon = cvet/14%, skruglenie 8dp, ikonka 20dp)
- Po centru: tekst (zagolovok 15sp ves 700 + podzagolovok 12sp seryy)
- Sprava: `arrow_forward_ios_rounded` cveta elementa, 15dp
- Mejudu plitkami: 8dp

---

## 8. EKRAN «DOBAVIT' TAYTL» (3 shaga)

Otkryvaetsya pri najatii na FAB. Tryohshagovyy master dobavleniya s indikatorom shagov.

### AppBar
- Knopka «Nazad/Zakryt'»: na shage 1 — `close_rounded`, na shagah 2–3 — `arrow_back_rounded`
- Zagolovok **«Dobavit' taytl»**
- Pod zagolovkom — indikator shagov (vysota 48dp)

### Indikator shagov (_StepIndicator)
Gorizontal'nyy ryad iz 3 sekciy, otstupy: 16dp sleva/sprava, 12dp snizu.

Kajdaya sekciya:
- Tonkaya gorizontal'naya poloska vysotoy 3dp: esli shag aktivon ili proyden — `#FF9F0A`, inache seryy/25%. Skruglenie 2dp. Animaciya smeny za 280ms.
- Pod poloskoy (5dp otstup) — nazvanie shaga 11sp: aktivnyy — `#FF9F0A` ves 700; proydennyy — seryy ves 500; budushchiy — seryy/50% ves 500

Nazvaniya shagov: **«Oblojka»**, **«Status»**, **«Dannye»**

Mejudu sekciyami: 8dp. Proydennye shagi klikabel'ny (mojno vernut'sya nazad).

### Nijnyaya knopka «Dalee / Dobavit'»
Postoyanno vidna vnyzu ekrana (SafeArea), vysota 52dp, gorizontal'nye otstupy 16dp, nijniy 16dp.
- Fon: `#FF9F0A`, skruglenie 12dp, bez teni
- Tekst: «Dalee» (shagi 1–2) ili «Dobavit'» (shag 3), belyy, 16sp, ves 700
- Ikonka sprava ot teksta: `arrow_forward_rounded` (shagi 1–2) ili `check_rounded` (shag 3), belaya 20dp
- Rasstoyanie mejdu tekstom i ikonkoy: 6dp

---

### Shag 1 — Oblojka i nazvanie

Soderjymoe prokrut_ivaetsya, verhniy otstup 24dp, bokovye i nijniy 16dp.

**Blok oblojki (po centru):**
- Razmer: 138×200dp, skruglenie 16dp
- Animaciya konteynera pri poyavlenii oblojki: 200ms
- **Esli oblojki net**: poverh konteynera risuetsya punktirnaya ramka (`#FF9F0A/40%`, tolshchina 1.5dp, shtrihi 6dp s probelami 4dp), skruglenie 16dp. V centre — ikonka `camera_alt_rounded` cvet `#FF9F0A` 32dp i podpis' «Oblojka» (11sp, ves 700, `#FF9F0A`).
- **Esli oblojka zagrujena**: izobrajenie zapolnyaet konteyner (fit: cover). Snizu gradientnyy overley ot prozrachnogo k `black/72%` s tekstom «Izmenit'» (belyy, 11sp, ves 700) v centre. Takje ten' konteynera: cvet oblojki s 35% prozrachnost'yu, blur 24dp, smeshchenie vniz 8dp.

Pri najatii na blok oblojki → otkryvaetsya shtorka vybora istochnika (sm. nije).

**Blok nazvaniya:**
Otstup sverhu 32dp. Metka **«NAZVANIE»** (seryy, 11sp, ves 700, interval bukv 0.6). Pole vvoda 16sp, ves 600. Podskazka «Vvedite nazvanie...». Avto-fokus na pole.

Nije polya — stroka s ikonkoy `info_outline_rounded` (14dp seraya) i tekstom «Oblojka neobyazatel'na» (12sp seryy).

### Shtorka vybora istochnika oblojki
- Dve plitki: «Iz galerei» (ikonka `photo_library_rounded`) i «По URL» (ikonka `link_rounded`)
- Kajdaya plitka: ikonka v konteynere s fonom `#FF9F0A/12%` + tekst 16sp ves 600
- Pri vybore «Po URL» → otkryvaetsya dialog s polem vvoda ssylki, knopki «Otmena» (seryy) i «Sohranit'» (`#FF9F0A`, ves 700)

---

### Shag 2 — Status i format

Kontent prokrut_ivaetsya, otstupy analogichny shagu 1.

**Blok «STATUS»:**
Metka seraya. Nije — 5 knopok vybora statusa (po odnoy na stroku, otstup 6dp mejdu nimi).

Kajdaya knopka:
- Gorizontal'nyy ryad: krujok statusa 10dp + tekst statusa 15sp + galochka sprava (tol'ko u aktivnoy)
- **Neaktivnaya**: fon = cvet kartochki, ramka tonkaya (dark: white/12%, light: black/12%)
- **Aktivnaya**: fon = cvet statusa s 10% prozrachnost'yu, ramka cveta statusa 1.5dp, tekst cveta statusa ves 700, ikonka `check_circle_rounded` sprava
- Vnutrennie otstupy: 16dp gorizont., 13dp vert., skruglenie 12dp
- Animaciya smeny fona/ramki: 150ms

**Blok «FORMAT IZDENIYA»:**
Metka seraya. Nije — edinyy konteyner s fonom kartochki, skruglenie 12dp, vnutrenniy otstup 4dp. Vnutri — spisok RadioListTile s razdelitelyami mejdu nimi.

Formaty (RadioListTile):
1. **LN+WN Gibrid** (tol'ko esli v nastroykah vklyuchyon «Gibridnyy format»)
   - Ikonka `bolt_rounded`
   - Podzagolovok: «Kompleksnyy format (LN toma + WN ongoing glavy)»
2. **Seriya tomov** — ikonka `layers_rounded`, podzagolovok «Seriynoe izdanie pechatnyh tomov (LN / Knigi)»
3. **Web-novella** — ikonka `language_rounded`, podzagolovok «Aziatskie web-romany, razbitye strogo po glavam (WN)»
4. **Singl (Odinochnoe)** — ikonka `menu_book_rounded`, podzagolovok «Odinochnyy roman (Onoshot / Tom-singl)»

Aktivnaya radioknopka: cvet `#FF9F0A`. Zagolovki formatov: 14sp, ves 700. Podzagolovki: 12sp, seryy.

**Pereklyuchatel' «Uchityvat' toma»:**
Pokazyvaetsya tol'ko esli vybran format NE Web i NE Gibrid. Konteyner s fonom kartochki, vnutri — stroka pereklyuchatelya s zagolovkom «Uchityvat' toma» i podzagolovkom «Otklyuchite dlya izdaniy bez tomov».

---

### Shag 3 — Dannye

Kontent prokrut_ivaetsya. Soderjit neskol'ko blokov (pokazyvayutsya v zavisimosti ot vybrannogo formata i nastroyek).

**Blok «ZAKLADKA»** (esli v nastroykah vklyucheny zakladki):
- Metka «ZAKLADKA»
- Pole vvoda s ikonkoy-prefiksom `bookmark_rounded` cvet `#FF9F0A` i podskazkoy «Vpishite glavu/tom, naprimer: 1.4 glava, 1h3.3»

**Blok «START POSLE ADAPTACII»** (esli v nastroykah vklyuchena dannaya funkciya):
- Metka «START POSLE ADAPTACII»
- Esli format «Seriya» ili «Singl»: pole s metkoy «Nachal'nyy tom (s kakogo nachali)» i ikonkoy `play_arrow_rounded` `#FF9F0A`
- Esli format «Web» ili «Gibrid»: pole с metkoy «Nachal'naya glava (s kakoy nachali)» i ikonkoy `play_arrow_rounded` `#FF9F0A`

**Blok «OCENKA TAYTLA»** (esli v nastroykah vklyuchena ocenka):
- Metka «OCENKA TAYTLA»
- Konteyner (fon kartochki, skruglenie 12dp, vertikal'nye otstupy 12dp, gorizontal'nye 10dp)
- Ryad zvyozdochek po centru: esli shkala 5 — 5 krupnyh zvyozdochek (32dp), esli shkala 10 — 10 nebol'shih (24dp)
- Aktivnye zvyozdy: `star_rounded` cvet `#FF9F0A`. Neaktivnye: `star_outline_rounded` seryy
- Pod zvyozdami (esli vybrana ocenka): tekst «Vybrano: N iz M» — 13sp ves 700 cvet `#FF9F0A` + knopka «Sbrosit'» 12sp cvet `#F87171` ves 600

**Blok «PROGRESS GLAV WEB-NOVELLY»** (tol'ko dlya formata «Web»):
- Metka
- Dva polya в ryad: «Prochitano glav» + «Vsego glav» (s podskazkoy «Neobyaz.»)
- U pervogo polya: ikonka `format_list_numbered_rounded` seraya. U vtorogo: `bookmark_border_rounded` seraya

**Blok «PROGRESS WEB-GLAV (V GIBRIDE)»** (tol'ko dlya formata «Gibrid»):
- Analogichno bloku web-glav, no metka «PROGRESS WEB-GLAV (V GIBRIDE)»

**Blok «SLOVA I RASCHYOTY»:**
Metka seraya. Dalee:

Esli format NE «Web» i toma uchityvayutsya:
- Pereklyuchatel' «Raschyot po tomam» (podzagolovok: «Zapisyvat' slova kajdogo toma» / «Vvesti summarno po knige»)
- **Esli «Raschyot po tomam» vklyuchyon**: spisok strok dlya kajdogo toma
  - Kajdaya stroka: pole «Tom» (76dp shirina, desyatichnyy vvod) + pole «Slov» (rastyagivaetsya) + knopka udaleniya (ikonka `remove_rounded` cvet `#F87171`, fon `#F87171/10%`, skruglenie 8dp)
  - Pod spiskom — knopka «+ Dobavit' tom» (outlined, ramка `#FF9F0A`, tekst `#FF9F0A`, na vsyu shirinu, vysota knopki avto)
  - Esli hot' odin tom est' — itogovyy boks: fon `#FF9F0A/12%`, ramka `#FF9F0A/20%`, skruglenie 12dp. Tekst: «Tomov: N» i «Slov: N» — oba `#FF9F0A`, ves 700
- **Esli «Raschyot po tomam» vyklyuchyon**: dva polya v ryad — «SLOV» (ikonka `text_fields_rounded`) + «TOMOV» (ikonka `layers_rounded`)

**Blok «VSEGO TOMOV V SERII»** (esli toma uchityvayutsya):
- Pereklyuchatel' «Ongoing» (podzagolovok: «Otobrajaetsya kak 5/?» / «Kol-vo tomov izvestno»)
- Esli ongoing vyklyuchyon: pole vvoda «Neobyazatel'no — napr. 25» s ikonkoy `bookmark_border_rounded`

---

## 9. EKRAN «REDAKTIROVAT' TAYTL»

Otkryvaetsya pri najatii na kartochku v biblioteke.

### AppBar
- Zagolovok **«Redaktirovat'»**
- Sprava: ikonka `check_rounded` cvet `#FF9F0A` — sohranit' izmeneniya

### Soderjymoe (prokrut_ivaemyy spisok)
Otstupy: 16dp so vseh storon, nijniy 60dp.

**Verhniy blok:**
Gorizontal'nyy ryad — oblojka + nazvanie.

Blok oblojki (80×110dp, skruglenie 8dp):
- Esli est' oblojka: izobrajenie + snizu gradientnyy overley s tekstom «Изменить» (belyy, 9sp, ves 700). Ten': cvet oblojki 30%, blur 10dp, smeshchenie vniz 4dp.
- Esli net: punktirnaya ramka + ikonka `camera_alt_rounded` 24dp + «Oblojka» 9sp (analog shaga 1)

Blok nazvaniya (sprava, rastyagivaetsya):
- Metka «NAZVANIE»
- Pole vvoda (pervaya zaglavnaya bukva) 15sp ves 600, podskazka «Введите название...»

**Blok «STATUS»:**
Metka. Vybor statusa — gorizontal'nye plitki (Wrap, spacing 8dp):
- Kajdyy status: krujok 8dp + probel + tekst 13sp
- Aktivnyy: fon = cvet statusa 12%, ramka = cvet statusa 1.5dp, tekst = cvet statusa ves 700
- Neaktivnyy: fon kartochki, ramka dark/12% ili light/12%
- Vnutrennie otstupy: 12dp gor., 8dp vert., skruglenie 10dp
- Animaciya 140ms

**Blok «FORMAT IZDENIYA»:**
Takoy je, kak v shage 2 (RadioListTile v konteynere). Vybor avtomaticheski sbrasyvaet konfliktuyushchie polya.

Pereklyuchatel' «Uchityvat' toma» — takoy je, kak v shage 2.

**Dal'neyshie bloki** (dannye, zakladki, ocenka, toma i t.d.) — identichny shagu 3 dobavleniya.

---

## 10. EKRAN «ANALITIKA»

### AppBar
- Zagolovok **«Analitika»**, standartnyy stil'

### Soderjymoe (prokrut_ivaemyy spisok)
Otstupy: 16dp so vseh storon, nijniy 40dp.

**Verhniy blok — kartochki metrik:**

Dva rejima (pereklyuchayutsya v nastroykah — «Shirokie kartochki statistiki»):

*Rejim «V ryad» (po umolchaniu):*
Gorizontal'nyy ryad s ravnymi dolyami, vysota odinakovaya u vseh (IntrinsicHeight):
- **«Zaversheno seriy»** — ikonka `emoji_events_rounded` cvet `#34D399`
- **«Zaversheno web»** — ikonка `language_rounded` cvet `#A78BFA` (tol'ko esli vklyucheno «Pokaz Web v analitike»)
- **«Prochitano tomov»** — ikonka `layers_rounded` cvet `#60A5FA` (tol'ko esli est' taytly s uchotom tomov)

*Rejim «Stopkoy»:*
Kartochki vystraivayutsya vertikal'no, kajdaya na vsyu shirinu. Mejudu nimi otstup 10dp.

Pod osnovnym ryadom (otstup 10dp) — kartochka na vsyu shirinu:
- **«Prochitano slov»** — ikonka `text_fields_rounded` cvet `#FF9F0A`

### Komponent kartochki metriki (_StatCard):
- Fon: cvet kartochki temy, skruglenie 12dp, vnutrennie otstupy 16dp
- Ikonka v konteynere (8dp vnutr. otstupy, fon = cvet ikonki 12%, skruglenie 8dp, ikonka 18dp)
- Nije (10dp otstup) — bol'shoe chislo: 28sp, ves 800, height 1.1, avtomaticheski umen'shaetsya esli ne pomeshchaetsya
- Nije (2dp otstup) — metka: 12sp, ves 500, seryy

**Blok «PO STATUSAM»:**
Metka «PO STATUSAM» seraya.

Edinyy konteyner s fonom kartochki i skrugleniem 12dp. Vnutri — 5 strok (po odnoy na status), razdelyonnyh tonkimi liniyami.

Kajdaya stroka:
- Gorizontal'nyy ryad: krujok 9dp cveta statusa + probel 10dp + nazvanie statusa 14sp ves 600 + beydj s kolichestvom sprava
- Beydj: fon = cvet statusa 12%, cvet teksta = cvet statusa, shrirft 13sp ves 800, gorizont. otstupy 10dp, vert. 3dp, skruglenie 8dp
- Pod ryadom (8dp otstup): progress-bar vysotoy 4dp, skruglenie 3dp, fon = cvet statusa 10%, zapolnenie = cvet statusa. Znachenie = (kol-vo knig dannogo statusa) / (vsego knig)

**Plitka «Nastroyki»:**
Knopka-ssylka v nijney chasti analitiki — perehod na ekran nastroek.
- Konteyner (fon kartochki, skruglenie 12dp, otstupy 16dp gor. / 14dp vert.)
- Gorizontal'nyy ryad: ikonka `settings_rounded` v akcentnom fone + tekst «Настройки» / «Управление функциями, тема, экспорт» + `chevron_right_rounded` seraya

---

## 11. EKRAN «NASTROYKI»

### AppBar
- Zagolovok **«Nastroyki»**

### Soderjymoe (prokrut_ivaemyy spisok)
Otstupy: 16dp, nijniy 40dp.

Vse bloki oformleny v vide «grupp» (_CardGroup): konteyner s fonom kartochki, skruglenie 12dp. Vnutrennie elementy razdelyeny tonkimi liniyami. Pered kajdoy gruppoy — metka seraya zaglavnymi bukvami.

---

**Blok «TEMA»:**

Tri stroki vybora temy:
- **AMOLED** — ikonka `dark_mode_rounded`
- **Tyomnaya** — ikonka `nightlight_round_rounded`
- **Svetlaya** — ikonka `wb_sunny_rounded`

Kajdaya stroka:
- Gorizontal'nyy ryad: ikonka (20dp) + tekst (15sp, rastyagivaetsya) + indikator sprava
- **Aktivnaya**: ikonka `#FF9F0A`, tekst `#FF9F0A` ves 700, sprava `check_circle_rounded` `#FF9F0A`
- **Neaktivnaya**: ikonka seraya, tekst seryy ves 500, sprava `circle_outlined` seraya
- Otstupy vnutri stroki: 16dp gor., 14dp vert.

---

**Blok «DOPOLNITEL'NYY FUNKCIONAL»:**

Komponent stroki s pereklyuchatelem (_SwitchRow):
- Gorizontal'nyy ryad: tekst (zagolovok 15sp ves 600 + podzagolovok 12sp seryy) + Switch
- Esli pereklyuchatel' zablokirovan (net onChanged): vsya stroka s 38% prozrachnost'yu
- Otstupy: 16dp gor., 11dp vert.

Stroki:

1. **«Zakladki»** — pri vkl: «Поле введения текущей главы без влияния на статистику»; pri vykl: «Поле заметок отключено»

2. **«Raspolojenie zakladki»** (pokazyvaetsya tol'ko esli zakladki vklyucheny):
   - Ne pereklyuchatel', a stroka s tekstom «Расположение закладки» (15sp ves 600) + DropdownButton
   - Dropdown: «Снизу» (znachenie 0) or «В ряд» (znachenie 1)
   - Cvet teksta Dropdown: `#FF9F0A`, ves Bold, 15sp
   - Fon vypadayushchego spiska: cvet kartochki, bez podchyorkivaniya

3. **«Chitat' posle adaptacii»** — pri vkl: «Возможность указать том/главу, с которых вы начали»; pri vykl: «Функция "Старт после адаптации" отключена»

4. **«Gibridnyy format LN+WN»** — pri vkl: «Позволяет объединить LN и WN в одной карточке»; pri vykl: «Раздельные карточки томов и глав»

5. **«Ocenka taytlov»** — pri vkl: «Возможность оценивать тайтлы»; pri vykl: «Функция выставления оценки отключена»

6. **«Shkala ocenki»** (pokazyvaetsya tol'ko esli ocenka vklyuchena):
   - Stroka «Шкала оценки» + DropdownButton
   - Znacheniya: «5 звёзд» (5) and «10 звёзд» (10)
   - Oformlenie Dropdown analogichno raspolojeniyu zakladki

---

**Blok «OTOBRAJENIE»:**

1. **«Pokaz Web v analitike»** — pri vkl: «Отображает прочитанные веб-новеллы в статистике и карточках»; pri vykl: «Скрывает метрики веб-новелл в отчётах»

2. **«Oblojki taytlov»** — pri vkl: «Показывать обложки в списке»; pri vykl: «Компактный вид без обложек»

3. **«Sokrashchat' chisla»** — «150K vmesto 150 000»

4. **«Shirokie kartochki statistiki»** — «Метрики друг под другом»

5. **«Knopka «Podelit'sya»»** — «В шапке библиотеки»

6. **«Skryt' nijniy bar»** — pri vkl: «Статистика через кнопку вверху»; pri vykl: «Нижняя навигация активна»

7. **«Glavy dlya Web-romanov»** — pri vkl: «Показывать X/Y гл. на карточках»; pri vykl: «Прогресс глав скрыт»

---

**Blok «DANNYE»:**

Dva elementa — «plitki deystviy» (_ActionTile):

1. **«Eksport biblioteki»**
   - Ikonka `upload_file_rounded` cvet `#34D399`
   - Podzagolovok «Сохранить в JSON-файл»
   - Pri najatii: sohranit' dannye v JSON fayl i vyzvat' sistemnyy dialog «Podelit'sya»

2. **«Import biblioteki»**
   - Ikonka `download_for_offline_rounded` cvet `#60A5FA`
   - Podzagolovok «Загрузить из JSON-файла»
   - Pri najatii: otkryt' sistemnyy faylovyy piker (tol'ko JSON fayly), zatem dialog podtverjdeniya

### Komponent «Plitka deystviya» (_ActionTile):
- Gorizontal'nyy ryad: ikonka v kvadratnom konteynere (8dp vnutr., fon = cvet/12%, skruglenie 8dp) + zagolovok 15sp ves 600 + podzagolovok 12sp seryy + `chevron_right_rounded` seraya 20dp sprava
- Otstupy: 16dp gor., 13dp vert.

### Dialog podtverjdeniya importa:
- Fon kartochki, skruglenie 20dp
- Zagolovok «Импорт библиотеки» 17sp ves 800
- Tekst «Будет загружено N тайтлов. Текущая библиотека будет заменена.» 14sp seryy
- Knopki: «Отмена» (seryy) and «Заменить» (`#FF9F0A`, ves 700)

### Uvedomleniya (Snackbar):
- Plavayushchie, otstupy: snizu 20dp, boka 16dp, skruglenie 12dp
- Uspeshnye operacii: fon `#FF9F0A`
- Oshibki: fon `#F87171`
- Tekst: shrirft Plus Jakarta Sans, ves 600

---

## 12. EKRAN «PODELIT'SYA — ANALITIKA»

### AppBar
- Zagolovok **«Analitika»**

### Soderjymoe
Po centru vertikal'no, prokrut_ivaetsya, otstupy 24dp.

**Kartochka dlya publikacii:**
- Shirokaya, polnost'yu rastyagnuta. Vnutrennie otstupy 24dp. Skruglenie 20dp.
- **Fon (tyomnaya tema)**: diagonal'nyy gradient sleva-sverhu vpravo-vniz, ot `#1A1024` do `#0F0A1A`
- **Fon (svetlaya tema)**: gradient ot `#FFF8EC` do `#FFF0D0`
- Ramka: white/6% (tyomnaya) or black/6% (svetlaya)

Vnutri kartochki:

*Shapka:*
- Gorizontal'nyy ryad: ikonka `auto_stories_rounded` v konteynere (10dp vnutr., fon `#FF9F0A/12%`, skruglenie 12dp, ikonka `#FF9F0A` 22dp) + tekst «ReadTracker» (17sp ves 900 cvet `#FF9F0A`) / «Моя статистика» (12sp seryy)

*Metriki (sverhu vniz, 14dp mejdu strokami):*
Kajdaya stroka (_ShareMetricRow):
- Ikonka v konteynere (8dp vnutr., fon = cvet ikonki/12%, skruglenie 8dp, ikonka 18dp)
- Metka (14sp, seryy, ves 500), rastyagivaetsya
- Znachenie sprava (22sp, ves 800)

Stroki:
1. Ikonka `emoji_events_rounded` `#34D399` — «Завершено серий» — chislo
2. Ikonка `language_rounded` `#A78BFA` — «Завершено веб-новелл» — chislo (tol'ko esli vklyuchyon pokaz web)
3. Ikonka `layers_rounded` `#60A5FA` — «Прочитано томов» — chislo (tol'ko esli est' knigi s tomami)
4. Ikonka `text_fields_rounded` `#FF9F0A` — «Прочитано слов» — chislo

**Knopka «Sohranit' v galereyu»:**
- Na vsyu shirinu, vysota avto (vertikal'nye otstupy 15dp), skruglenie 12dp
- Fon: `#FF9F0A`
- Ikonka `download_rounded` belaya sleva + tekst «Сохранить в галерею» belyy 15sp ves 700
- Vo vremya sohraneniya: pokazyvaetsya spinner (18×18dp, belyy) and tekst «Сохраняем...», knopka neaktivna
- Posle uspeshnogo sohraneniya: Snackbar «Сохранено в галерею» s zelyonym fonom `#34D399` and ikonkoy `check_circle_rounded`

---

## 13. EKRAN «PODELIT'SYA — SPISOK TAYTLOV»

### AppBar
- Zagolovok **«Список тайтлов»**

### Soderjymoe
Prokrut_ivaetsya, otstupy 24dp.

**Kartochka dlya publikacii:**
- Vnutrennie otstupy 24dp, skruglenie 20dp, na vsyu shirinu
- **Fon (tyomnaya tema)**: diagonal'nyy gradient ot `#0D1A14` do `#061008`
- **Fon (svetlaya tema)**: gradient ot `#ECFDF5` do `#D1FAE5`
- Ramka: white/6% or black/6%

Vnutri kartochki:

*Shapka:*
- Gorizontal'nyy ryad: ikonka `format_list_bulleted_rounded` v konteynere (fon `#34D399/15%`, skruglenie 12dp, ikonka `#34D399` 22dp) + tekst «ReadTracker» (17sp ves 900 `#34D399`) / «Тайтлов: N» (12sp seryy)

*Stroka itogov* (esli vklyuchyon pokaz web):
- Tekst «Серий завершено: N  |  Веб завершено: N» — 12sp ves Bold cvet `#34D399`

*Spisok taytlov:*
Kajdyy taytl (vertikal'nyy otstup snizu 9dp):
- Gorizontal'nyy ryad: cvetnaya poloska 3×44dp cveta statusa (skruglenie 2dp) + 10dp otstup + informaciya
- Informaciya: nazvanie (13sp ves 700, 1 stroka) + esli est' ocenka — ryadom so skrollom vpravo tekst ocenki (11sp `#FF9F0A` Bold)
- Stroka nije: status sleva (11sp cvet statusa ves 600) + slova sprava (11sp seryy) + toma (11sp seryy)

**Knopka «Sohranit' v galereyu»:**
- Analogichno ekranu analitiki, no fon knopki `#34D399`
- Snackbar pri uspehe s fonom `#34D399`

---

## 14. FORMATIROVANIE CHISEL

Esli v nastroykah vklyucheno «Сокращать числа»:
- ≥ 1 000 000 → «1.2M»
- ≥ 1 000 → «150K»

Esli vyklyucheno: chisla formatiruyutsya s probelom kak razdelitel' tysyach: «150 000».

---

## 15. BEYDJI TIPA (_Badge)

Ispol'zuyutsya na kartochkah taytlov dlya oboznacheniya formata i drugih priznakov.

Oformlenie:
- Fon = cvet beydja s 10% prozrachnost'yu
- Tekst = cvet beydja, shrirft 10sp, ves 700
- Skruglenie 6dp
- Otstupy: 6dp gorizontal'nye, 2dp vertikal'nye

Perechen':
- «LN+WN» — `#FF9F0A`
- «Серия» — `#A78BFA`
- «Веб» — `#FBBF24`
- «Сингл» — `#FF9F0A`
- «Онг.» — `#34D399`
- «Старт: т. N» — `#34D399`
- «Старт: гл. N» — `#34D399`

---

## 16. HRANENIE DANNYH

Vse dannye hranyatsya lokal'no. Pri kajdom izmenenii dannye avtomaticheski sohranyayutsya.

Sohranyaemye nastroyki (klyuchi):
- `themeMode` (int, po umolch. 1)
- `shortenNumbers` (bool, po umolch. false)
- `showShareButton` (bool, po umolch. true)
- `stackedStats` (bool, po umolch. false)
- `showCovers` (bool, po umolch. true)
- `hideBottomBar` (bool, po umolch. false)
- `showWebChapters` (bool, po umolch. false)
- `showBookmarks` (bool, po umolch. true)
- `bookmarkPosition` (int, 0=snizu / 1=v ryad, po umolch. 0)
- `enableAdaptationStart` (bool, po umolch. false)
- `enableHybrid` (bool, po umolch. true)
- `enableRating` (bool, po umolch. true)
- `ratingScale` (int, 5 или 10, po umolch. 10)
- `showWebInStats` (bool, po umolch. true)
- `savedTabIndex` (int, po umolch. 0) — zapominaet poslednyuyu vkladku biblioteki
- `books` (JSON-stroka, massiv taytlov)

### Model' taytla (Book):
- `id` — stroka (unikal'nyy identifikator)
- `title` — stroka
- `status` — chislo 0–4 (planned, reading, paused, completed, dropped)
- `isSeries`, `isWeb`, `isSingle` — bool
- `countVolumes` — bool (uchityvat' toma)
- `words`, `volumes` — int
- `totalVolumesInSeries` — int? (obshchee chislo tomov serii)
- `isOngoing` — bool
- `coverColor` — int (ARGB cvet)
- `coverUrl` — string? (URL oblojki)
- `localImagePath` — string? (lokal'nyy put')
- `useDetailedVolumes` — bool
- `volumeEntries` — spisok ob'ektov `{v: double, w: int}` (tom, slov)
- `webChapters`, `totalWebChapters` — int?
- `currentBookmark` — string? (tekst zakladki)
- `isHybridFormat` — bool
- `hybridWebChapters`, `hybridTotalWebChapters` — int?
- `rating` — int? (hranyatsya v shkale do 10; pri otobrajenii v 5-zvyozdochnom delitsya na 2)
- `startVolume`, `startChapter` — int?

### Vychislimye polya:
- `effectiveWords` — esli vklyuchyon raschyot po tomam: summa slov iz volumeEntries; inache pole words
- `effectiveVolumes` — esli vklyuchyon raschyot po tomam: dlina volumeEntries; inache pole volumes
- `volumeLabel()` — formiruet stroku: «N/? т.» (ongoing), «N/M т.» (izvestno vsego), «N т.» (bez ukazaniya)
- `chapterLabel()` — «N/M gl.» or «N gl.»
- `getRatingDisplay(scale)` — «N/10 ★» or «N/5 ★»

---

## 17. EKSPORT I IMPORT

**Eksport:**
Dannye serializuyutsya v JSON s otstupami (pretty print). Fayl nazyvaetsya `readtracker_backup_TIMESTAMP.json`. Vyzyvaetsya sistemnyy dialog «Podelit'sya» dlya sohraneniya ili otpravki fayla.

**Import:**
Otkryvaetsya sistemnyy faylovyy piker (fil'tr tol'ko `.json`). Posle vybora fayla pokazyvaetsya dialog podtverjdeniya s ukazaniem kolichestva taytlov. Pri podtverjdenii — tekushchiy spisok taytlov polnost'yu zamenyaetsya importirovannym. Pri uspehe — Snackbar «Загружено N тайтлов». Pri oshibke — Snackbar krasnyy «Ошибка импорта: ...».

---

## 18. SKROLLING

Vezde primenyaetsya «prujinnaya» fizika prokrut_ki (BouncingScrollPhysics) — pri dostijenii krayov spisok prujinit. Nativnyy effekt overscroll otklyuchyon (bez tyomnogo kruga-volny pri pereskrolle).

---

## 19. OBSHCHIE INTERAKTIVNYE PATTERNY

- Vse dialogi imeyut fon cveta kartochki tekushchey temy i skruglenie 20dp
- Knopki в dialogah: TextButton bez fona
- Vse modal'nye shtorki snizu: fon kartochki, skruglenie 20dp sverhu
- Pri poyavlenii shtorki — indikator-«pilyulya» 36×4dp seryy/30% sverhu po centru
- Animacii smeny sostoyaniy knopok/kartochek: 140–200ms
- Tekstovye metki-zagolovki razdelov: «ЗАГЛАВНЫЕ БУКВЫ», 11sp, seryy, ves 700, spacing 0.6. Nijniy otstup pered soderjimym: 8dp.
