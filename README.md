
# WalkNMap
*Work in progress*

Небольшой pet-project, который запоминает все посещенные пользователем места и отображает это на карте.
На данный момент это устроено так - карта мира разбита на "квадраты" по сетке широта-долгота, не посещенная часть карты отображается затемненной, посещенные квадраты - как обычно.

**Внимание**: в репозитории отсутствует файл с `const val API_KEY`, в котором должен храниться API-ключ для работы Yandex MapKit'a

## Функционал
Для отображения карты мира используются карты от Яндекса. 
Кнопки в углу экрана позволяют перемещаться к местоположению пользователя, включать два режим слежения, с поворотом карты и без, а также выравнивать карту на базовое направление (север - сверху, камера смотрит ровно вниз).

Посещенные квадраты показываются на карте как обычно, не посещенная часть - затемненной. При заходе в не посещенный квадрат он становится полностью видимым и добавляется в базу данных посещенных квадратов.

## Техническая сторона
Координаты посещенных квадратов хранятся в SQL датабазе, доступ к ним осуществляется через Room. Посещенные квадраты как livedata передаются во Viewmodel, для отображения на экране вытаскивают лишь те квадраты, что попадают в обозримую на экране зону. 

Не посещенная зона отображается через два метода:
1. Обрамляющий прямоугольник - определяются максимальные и минимальные координаты всех посещенных квадратов на экране и вокруг них рисуется затемненный полигон.
2. Внутри обрамляющего прямоугольника закрашиваются все не посещенные квадраты.

Эти две функции делаются через систему MapObject'ов для Yandex Mapkit'а.

## Road map

 - [x] Система округления координат до определенных значений для разделения карты мира на двухкоординатную систему прямоугольников.
 - [x] Сохранение через Room в базу данных центры посещенных прямоугольников по кнопке.
 - [x] Отрисовка посещенных прямоугольников как Polygon MapObject на карте.
 - [x] Добавление посещенных прямоугольников автоматически при заходе геопозиции в новый не посещенный прямоугольник.
 - [x] Добавление времени и даты первого посещения прямоугольника для будущей фильтрации по времени.
 - [x] Отрисовка только тех посещенных прямоугольников, что видны сейчас на экране.
 - [x] "Туман войны" - система отрисовки наоборот: на не посещенных территориях накладывается затемнение. Только на посещенных прямоугольниках ничего нет. 
 - [ ] Перерисовка затемняющих полигонов при перемещении камеры не каждый раз всех, а только тех, что появились только что.
 - [ ] Перемещение слоев затемняющих элементов выше всех остальных элементов карты для лучшего сокрытия не открытых территорий.
 - [ ] Добавление к сохраненным посещенным прямоугольникам номерной "сессии", когда они впервые были открыты.
 - [ ] Не постоянная запись пройденных мест, а только по кнопке, которая запускает процесс записи или останавливает. Каждая такая новая запись будет "сессией".
 - [ ] По еще одной кнопке затемнение экрана и скрытие карты для экономии батареи телефона.
 - [ ] (возможно) Изменение размера открываемых прямоугольников через настройки .
 - [ ] Поиск ближайших достопримечательностей с компасом до них. При открытии прямоугольника с ней, она добавляется в специальный список посещенных мест.
 - [ ] Фильтрование отображения открытых мест на карте по дате или по сессии.
 - [ ] Запись посещаемых мест с приложением в бэкграунде.
 - [ ] Запись посещаемых мест в заблокированном режиме.