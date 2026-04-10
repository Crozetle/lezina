# Лабораторная работа №1. Порождающие паттерны

## Содержание
1. [Структура проекта](#структура-проекта)
2. [Паттерн Singleton](#паттерн-singleton)
3. [Паттерн Factory Method](#паттерн-factory-method)
4. [Паттерн Prototype](#паттерн-prototype)
5. [Ответы на вопросы](#ответы-на-вопросы)
6. [Шпаргалка по Java для новичков](#шпаргалка-по-java)

---

## Структура проекта

```
lab1/
├── singleton/
│   ├── AppConfig.java          ← класс-одиночка, читает настройки
│   └── config.properties       ← файл настроек
│
├── factory/
│   ├── Transport.java          ← интерфейс транспортного средства
│   ├── Car.java                ← Автомобиль (массив моделей)
│   ├── Motorcycle.java         ← Мотоцикл (двусвязный список)
│   ├── TransportFactory.java   ← интерфейс + AutoFactory + MotoFactory + TransportUtils
│   ├── NoSuchModelNameException.java
│   ├── DuplicateModelNameException.java
│   └── ModelPriceOutOfBoundsException.java
│
└── prototype/
    ├── CarPrototype.java        ← Автомобиль с clone()
    └── MotorcyclePrototype.java ← Мотоцикл с clone()
```

### Как компилировать и запускать

```bash
# Singleton
cd singleton
javac AppConfig.java
java -cp . AppConfig

# Factory Method (все файлы вместе, т.к. ссылаются друг на друга)
cd factory
javac *.java
java TransportUtils

# Prototype
cd prototype
javac CarPrototype.java
java CarPrototype
javac MotorcyclePrototype.java
java MotorcyclePrototype
```

---

## Паттерн Singleton

### Что это такое?

Singleton («одиночка») — **порождающий паттерн**, который гарантирует существование **ровно одного** экземпляра класса и предоставляет глобальную точку доступа к нему.

### Зачем нужен?

Некоторые объекты по природе своей должны существовать в единственном экземпляре:

- **Файл настроек**: загружать конфигурацию несколько раз расточительно и опасно (разные части программы могут увидеть разные значения);
- **Пул соединений с БД**: управление ресурсами должно быть централизовано;
- **Логгер**: вывод в лог должен идти через один объект.

Без Singleton разные части программы могут случайно создать несколько копий объекта через `new`.

### Структурная схема

```
┌─────────────────────────────────────┐
│           AppConfig                 │
├─────────────────────────────────────┤
│ - INSTANCE: AppConfig  (static)     │  ← единственный экземпляр
│ - properties: Properties            │
├─────────────────────────────────────┤
│ - AppConfig()          (private)    │  ← конструктор закрыт
│ + getInstance(): AppConfig (static) │  ← единственная точка доступа
│ + getProperty(key): String          │
└─────────────────────────────────────┘
```

### Ключевые элементы реализации

| Элемент | Описание |
|---|---|
| `private static final AppConfig INSTANCE` | Единственный объект создаётся при загрузке класса |
| `private AppConfig()` | Приватный конструктор запрещает `new AppConfig()` снаружи |
| `public static getInstance()` | Единственный способ получить объект |

### Код из нашего примера

```java
// Запрещаем создание снаружи:
private AppConfig() {
    properties = new Properties();
    // ... загрузка файла ...
}

// Единственная точка доступа:
public static AppConfig getInstance() {
    return INSTANCE;  // всегда возвращает один и тот же объект
}
```

**Проверка единственности:**
```java
AppConfig c1 = AppConfig.getInstance();
AppConfig c2 = AppConfig.getInstance();
System.out.println(c1 == c2);  // true — один объект в памяти
```

### Особенности реализации (Eager vs Lazy)

В нашей реализации используется **ранняя инициализация (eager)**:
```java
private static final AppConfig INSTANCE = new AppConfig();
```
Объект создаётся при первой загрузке класса JVM. Это потокобезопасно из коробки.

Альтернатива — **ленивая инициализация (lazy)** с двойной проверкой блокировки:
```java
private static volatile AppConfig instance;

public static AppConfig getInstance() {
    if (instance == null) {
        synchronized (AppConfig.class) {
            if (instance == null) {
                instance = new AppConfig();
            }
        }
    }
    return instance;
}
```
Это нужно, если создание объекта очень тяжёлое и его лучше отложить.

---

## Паттерн Factory Method

### Что это такое?

Factory Method («фабричный метод») — **порождающий паттерн**, который определяет интерфейс для создания объекта, но позволяет подклассам изменить тип создаваемого объекта.

### Зачем нужен?

Представьте: у вас есть `TransportUtils` с методом `createInstance()`. Без паттерна пришлось бы писать:

```java
// Плохо: жёсткая привязка к конкретному классу
public static Transport createInstance(String brand, int size) {
    return new Car(brand, size);   // а если нужен мотоцикл?
}
```

Когда понадобится создавать мотоциклы — придётся менять сам метод. С Factory Method достаточно передать другую фабрику:

```java
TransportUtils.setTransportFactory(new MotoFactory());
Transport t = TransportUtils.createInstance("Honda", 2);  // теперь Motorcycle!
```

Клиентский код (`createInstance`) не знает ни о `Car`, ни о `Motorcycle`.

### Структурная схема

```
«interface»          «interface»
Transport            TransportFactory
    △                     △
    │                     │
    ├── Car            ├── AutoFactory ──────────► создаёт Car
    └── Motorcycle     └── MotoFactory ──────────► создаёт Motorcycle
                              ▲
                              │ хранит ссылку
                       TransportUtils
                         - factory: TransportFactory  (static)
                         + createInstance(brand, size): Transport
                         + setTransportFactory(f)
```

### Участники паттерна

| Участник | В нашем коде | Роль |
|---|---|---|
| Product (продукт) | `Transport` (интерфейс) | Что создаётся |
| ConcreteProduct | `Car`, `Motorcycle` | Конкретные реализации |
| Creator | `TransportUtils` | Использует фабрику |
| Factory (фабрика) | `TransportFactory` | Интерфейс создания |
| ConcreteFactory | `AutoFactory`, `MotoFactory` | Конкретное создание |

### Ключевые куски кода

**Интерфейс фабрики:**
```java
interface TransportFactory {
    Transport createInstance(String brand, int size);
}
```

**Конкретная фабрика:**
```java
class AutoFactory implements TransportFactory {
    @Override
    public Transport createInstance(String brand, int size) {
        return new Car(brand, size);  // знает о Car
    }
}
```

**Клиентский код (не знает о Car/Motorcycle):**
```java
class TransportUtils {
    private static TransportFactory factory = new AutoFactory();

    public static Transport createInstance(String brand, int size) {
        return factory.createInstance(brand, size);  // делегирует фабрике
    }
}
```

**Переключение типа создаваемых объектов:**
```java
// Создаём автомобиль (по умолчанию)
Transport car = TransportUtils.createInstance("Toyota", 2);

// Переключаем фабрику — теперь будут создаваться мотоциклы
TransportUtils.setTransportFactory(new MotoFactory());
Transport moto = TransportUtils.createInstance("Honda", 2);
```

### Структуры данных: массив vs список

**Car** использует массив `Model[]`:
- Быстрый доступ по индексу: `models[i]`
- При добавлении создаётся новый массив (`Arrays.copyOf`)
- При удалении — сдвиг элементов (`System.arraycopy`)

**Motorcycle** использует двусвязный циклический список с головой:
```
head ⇄ Model1 ⇄ Model2 ⇄ Model3 ⇄ (back to head)
```
- Добавление/удаление в середину O(1) (только перешиваем ссылки)
- Нет случайного доступа по индексу
- Голова (head) — фиктивный узел, упрощает граничные случаи

### Исключения в Java

В задании требуются три класса исключений:

| Класс | Тип | Когда выбрасывается |
|---|---|---|
| `NoSuchModelNameException` | checked (объявляемое) | Обращение к несуществующей модели |
| `DuplicateModelNameException` | checked (объявляемое) | Добавление модели с уже существующим именем |
| `ModelPriceOutOfBoundsException` | unchecked (необъявляемое) | Отрицательная цена |

**Checked** → наследует `Exception` → метод обязан объявить `throws`:
```java
public double getModelPrice(String name) throws NoSuchModelNameException { ... }
```

**Unchecked** → наследует `RuntimeException` → объявлять необязательно:
```java
public void addModel(String name, double price) { // нет "throws ModelPriceOutOfBoundsException"
    if (price < 0) throw new ModelPriceOutOfBoundsException(price);
    ...
}
```

---

## Паттерн Prototype

### Что это такое?

Prototype («прототип») — **порождающий паттерн**, который создаёт новые объекты путём **копирования** (клонирования) уже существующих.

### Зачем нужен?

- Создание объекта «с нуля» может быть дорогим (долгая инициализация, запросы в БД).
- Нужно получить объект, похожий на существующий, с минимальными изменениями.
- Количество возможных типов объектов неизвестно заранее.

```java
// Без Prototype — создаём «почти такой же» Car вручную:
Car copy = new Car(original.getBrand(), original.getModelCount());
for (String name : original.getModelNames()) {
    copy.addModel(name, original.getModelPrice(name));  // много кода + исключения
}

// С Prototype — просто клонируем:
Car copy = (Car) original.clone();  // одна строка
```

### Поверхностное vs Глубокое клонирование

```
ПОВЕРХНОСТНОЕ (shallow):
original ──► [brand="Toyota"] [models ──┐]
copy     ──► [brand="Toyota"] [models ──┘]  ← оба указывают на ОДИН массив!

Если изменить copy.models[0].price — изменится и original.models[0].price!

ГЛУБОКОЕ (deep):
original ──► [brand="Toyota"] [models ──► [Model1] [Model2]]
copy     ──► [brand="Toyota"] [models ──► [Model1'] [Model2']]  ← разные объекты
```

### Реализация в Java

**1. Реализуем интерфейс `Cloneable`** (маркерный, методов нет):
```java
public class CarPrototype implements Cloneable { ... }
```

**2. Переопределяем `Object.clone()`:**
```java
@Override
public Object clone() throws CloneNotSupportedException {
    // super.clone() — поверхностная копия (копирует поля как есть)
    CarPrototype clone = (CarPrototype) super.clone();

    // Глубокое клонирование массива:
    clone.models = new Model[this.models.length];
    for (int i = 0; i < this.models.length; i++) {
        if (this.models[i] != null) {
            clone.models[i] = (Model) this.models[i].clone();  // клон каждого узла
        }
    }
    // String неизменяем → brand клонировать не нужно

    return clone;
}
```

**3. Вызов:**
```java
CarPrototype original = new CarPrototype("Toyota", 2);
original.addModel("Camry", 35000);

CarPrototype copy = (CarPrototype) original.clone();
copy.getModels()[0].price = 99999;  // меняем копию

System.out.println(original.getModels()[0].price);  // 35000 — оригинал не изменился!
```

### Особенности клонирования связного списка (Motorcycle)

Для двусвязного списка `super.clone()` скопирует только ссылку `head`. Оба объекта будут разделять один и тот же список узлов. Поэтому нужно перестроить весь список:

```java
@Override
public Object clone() throws CloneNotSupportedException {
    MotorcyclePrototype clone = (MotorcyclePrototype) super.clone();

    // Создаём новую голову для клона
    clone.head = new Node();
    clone.head.prev = clone.head;
    clone.head.next = clone.head;
    clone.size = 0;

    // Обходим оригинал и создаём новые узлы для клона
    for (Node cur = this.head.next; cur != this.head; cur = cur.next) {
        Node newNode = new Node(cur.name, cur.price);
        clone.insertLast(newNode);
        clone.size++;
    }
    return clone;
}
```

---

## Ответы на вопросы

### 1. Abstract Factory (Абстрактная фабрика)

**Группа:** Порождающие паттерны.

**Описание:** Предоставляет интерфейс для создания **семейств связанных или зависимых объектов**, не указывая их конкретных классов.

**Отличие от Factory Method:** Factory Method создаёт **один** продукт; Abstract Factory — **семейство** продуктов.

**Назначение:** Создавать продукты, которые должны использоваться вместе (например, кнопка и чекбокс в одном стиле — Windows или Mac).

**Область применения:**
- GUI-фреймворки: один набор виджетов для разных ОС;
- Поддержка разных БД (MySQL-соединение + MySQL-команда + MySQL-транзакция).

**Особенности реализации:**
- Абстрактная фабрика — интерфейс/абстрактный класс с методом для каждого продукта;
- Конкретные фабрики реализуют все методы;
- Клиент работает только с интерфейсом фабрики.

**Структурная схема:**
```
«interface»
AbstractFactory
 + createProductA(): ProductA
 + createProductB(): ProductB
        △
        ├── ConcreteFactory1  → создаёт ProductA1 + ProductB1
        └── ConcreteFactory2  → создаёт ProductA2 + ProductB2
```

---

### 2. Builder (Строитель)

**Группа:** Порождающие паттерны.

**Описание:** Разделяет конструирование сложного объекта и его представление, позволяя одному процессу создавать разные представления.

**Назначение:** Создавать сложные объекты пошагово, не путая конструктор с десятками параметров.

**Область применения:**
- Построение сложных объектов с множеством опциональных параметров;
- Создание разных вариантов объекта одним и тем же процессом (деревянный дом, каменный дом).

**Особенности реализации:**
- Director управляет порядком вызовов;
- Builder определяет шаги;
- ConcreteBuilder реализует шаги для конкретного типа объекта;
- Метод `getResult()` возвращает готовый объект.

**Структурная схема:**
```
Director ──────────────► «interface» Builder
  - builder                + buildPartA()
  + construct()            + buildPartB()
                           + getResult(): Product
                                 △
                           ConcreteBuilder ──────► Product
```

---

### 3. Factory Method (Фабричный метод)

**Группа:** Порождающие паттерны.

**Описание:** Определяет интерфейс для создания объекта, оставляя подклассам решение о том, какой класс инстанциировать.

**Назначение:** Убрать прямую зависимость клиентского кода от конкретных классов создаваемых объектов.

**Область применения:**
- Заранее неизвестно, объект какого класса нужно создать;
- Нужно дать подклассам контроль над создаваемыми объектами;
- Создание объектов должно быть централизовано для удобства изменений.

**Особенности реализации:**
- Фабричный метод может возвращать уже существующий объект (кеширование);
- Параметризованный фабричный метод принимает тип объекта как параметр;
- В нашем коде: `TransportUtils.createInstance()` + интерфейс `TransportFactory`.

**Структурная схема:**
```
«interface»                «interface»
Transport                  TransportFactory
    △                           △
    ├── Car               ├── AutoFactory → new Car(...)
    └── Motorcycle        └── MotoFactory → new Motorcycle(...)
                                    ▲
                               TransportUtils.createInstance()
```

---

### 4. Prototype (Прототип)

**Группа:** Порождающие паттерны.

**Описание:** Задаёт виды создаваемых объектов с помощью экземпляра-прототипа и создаёт новые объекты путём копирования этого прототипа.

**Назначение:** Создавать объекты, копируя существующие, вместо создания через `new`.

**Область применения:**
- Создание объекта через `new` дорого (долгая инициализация);
- Классы для создания объектов неизвестны до выполнения программы;
- Нужно несколько вариантов «почти одинаковых» объектов.

**Особенности реализации:**
- В Java: реализовать `Cloneable` + переопределить `clone()`;
- `super.clone()` даёт поверхностную копию — нужно вручную клонировать изменяемые поля;
- `String` в Java неизменяем — его отдельно клонировать не нужно;
- Примитивные типы (`int`, `double`) копируются по значению — тоже не нужно клонировать отдельно.

**Структурная схема:**
```
«interface» Cloneable
        △
 CarPrototype
  + clone(): Object   ← переопределяем, делаем глубокое клонирование
  + brand: String
  + models: Model[]
        |
        └── Model implements Cloneable
             + clone(): Object
```

---

### 5. Singleton (Одиночка)

**Группа:** Порождающие паттерны.

**Описание:** Гарантирует, что класс имеет только один экземпляр, и предоставляет глобальную точку доступа к нему.

**Назначение:** Управлять доступом к единственному экземпляру класса.

**Область применения:**
- Когда необходим только один объект класса: логгер, кеш, пул потоков, настройки приложения;
- Когда глобальная переменная нежелательна, а доступ к объекту нужен из разных мест.

**Особенности реализации:**
- Приватный конструктор + статический метод `getInstance()`;
- Eager (ранняя) инициализация: объект создаётся при загрузке класса — потокобезопасно;
- Lazy (ленивая) с `synchronized`: объект создаётся при первом обращении;
- `enum`-Singleton — самая надёжная реализация в Java (защита от рефлексии и десериализации).

**Структурная схема:**
```
┌──────────────────────────────────────┐
│              AppConfig               │
├──────────────────────────────────────┤
│ - INSTANCE: AppConfig  {static}      │
│ - properties: Properties             │
├──────────────────────────────────────┤
│ - AppConfig()          {private}     │
│ + getInstance(): AppConfig {static}  │
│ + getProperty(key): String           │
└──────────────────────────────────────┘
```

---

## Шпаргалка по Java

### Ключевые слова

| Слово | Смысл |
|---|---|
| `static` | Принадлежит классу, а не объекту. Одно на все экземпляры. |
| `final` | Переменную нельзя переприсвоить; метод нельзя переопределить; класс нельзя наследовать. |
| `private` | Доступ только внутри своего класса. |
| `public` | Доступ откуда угодно. |
| `protected` | Доступ внутри класса и его наследников. |
| `interface` | Чистый контракт: объявляет методы, но не реализует (до Java 8). |
| `implements` | Класс берёт на себя обязательство реализовать интерфейс. |
| `extends` | Наследование от другого класса или интерфейса. |
| `@Override` | Аннотация: метод переопределяет родительский. Компилятор проверяет. |
| `this` | Ссылка на текущий объект. |
| `super` | Ссылка на родительский класс. |
| `new` | Создать новый объект в куче (heap). |
| `instanceof` | Проверить, является ли объект экземпляром класса. |
| `throws` | Метод может выбросить это исключение (для checked). |
| `throw` | Выбросить исключение прямо сейчас. |

### Сравнение объектов

```java
// НЕВЕРНО для строк и объектов:
if (str1 == str2)         // сравнивает ссылки (адреса в памяти)!

// ВЕРНО:
if (str1.equals(str2))    // сравнивает содержимое
```

`==` для примитивов (`int`, `double`, `boolean`) — сравнивает значения. Для объектов — сравнивает ссылки.

### Массивы

```java
String[] arr = new String[5];   // массив из 5 элементов, заполнен null
arr[0] = "hello";               // присваивание
int len = arr.length;           // длина массива (поле, не метод!)

// Копирование массива:
String[] copy = Arrays.copyOf(arr, arr.length + 1);  // создаёт новый массив

// Быстрое копирование участка:
System.arraycopy(src, srcPos, dst, dstPos, length);
```

### Цикл for-each

```java
for (String s : arr) {          // перебирает все элементы arr
    System.out.println(s);
}
// Эквивалентно:
for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
}
```

### Исключения

```java
try {
    // код, который может выбросить исключение
    double price = transport.getModelPrice("Unknown");
} catch (NoSuchModelNameException e) {
    // обрабатываем конкретный тип
    System.err.println("Ошибка: " + e.getMessage());
} catch (Exception e) {
    // перехватываем всё остальное
    e.printStackTrace();
} finally {
    // выполняется всегда (с исключением или без)
}
```

### Внутренние классы

```java
class Outer {
    class Inner { }              // нестатический: хранит ссылку на Outer
    static class StaticInner { } // статический: не зависит от Outer
    private class Private { }    // приватный: виден только внутри Outer
}
```

В задании:
- `Car.Model` — **нестатический** `private` внутренний класс (не нужен снаружи);
- `CarPrototype.Model` — **статический** (чтобы можно было клонировать независимо).
