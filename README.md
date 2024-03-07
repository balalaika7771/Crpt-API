# Класс CrptApi

Этот класс представляет собой инструмент для работы с API Честного знака. Он обеспечивает ограничение на количество запросов к API в определенный интервал времени, что помогает избежать превышения максимального количества запросов к API.

## Особенности

- Поддерживает ограничение на количество запросов к API в заданный интервал времени.
- При превышении лимита запрос вызов блокируется, чтобы не превысить максимальное количество запросов к API.
- Поддерживает thread-safe поведение для многопоточных сред.

## Использование

```java
// Создание экземпляра класса CrptApi с ограничением на 10 запросов в минуту
CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 1);

// Создание документа для ввода в оборот товара
Document document = new Document(new Document.Description("1234567890"), "doc123", "created", "LP_INTRODUCE_GOODS", true,
        "owner_inn", "participant_inn", "producer_inn", "2020-01-23", "production_type", new ArrayList<>(), "2020-01-23", "reg_number");
String signature = "signature_string";

// Вызов метода для создания документа
crptApi.createDocument(document, signature);
```

## Методы

### `public CrptApi(TimeUnit timeUnit, int requestLimit)`

Создает экземпляр класса CrptApi с указанным ограничением на количество запросов к API в заданный промежуток времени.

- `timeUnit`: промежуток времени для ограничения запросов (например, секунда, минута и т.д.).
- `requestLimit`: максимальное количество запросов в указанный промежуток времени.

### `public void createDocument(Document document, String signature)`

Создает документ для ввода в оборот товара.

- `document`: объект, представляющий документ.
- `signature`: строка с подписью документа.

## Примечание

Вызывается по HTTPS метод POST следующий URL:
[https://ismp.crpt.ru/api/v3/lk/documents/create](https://ismp.crpt.ru/api/v3/lk/documents/create)

В теле запроса передается в формате JSON документ:

```json
{
  "description": { 
    "participantInn": "string" 
  }, 
  "doc_id": "string", 
  "doc_status": "string", 
  "doc_type": "LP_INTRODUCE_GOODS", 
  "importRequest": true, 
  "owner_inn": "string", 
  "participant_inn": "string", 
  "producer_inn": "string", 
  "production_date": "2020-01-23", 
  "production_type": "string", 
  "products": [ 
    { 
      "certificate_document": "string", 
      "certificate_document_date": "2020-01-23", 
      "certificate_document_number": "string", 
      "owner_inn": "string", 
      "producer_inn": "string", 
      "production_date": "2020-01-23", 
      "tnved_code": "string", 
      "uit_code": "string", 
      "uitu_code": "string" 
    } 
  ], 
  "reg_date": "2020-01-23", 
  "reg_number": "string"
}
```

### Лицензия

Этот проект лицензируется по лицензии MIT - подробности смотрите в файле [LICENSE](LICENSE).
