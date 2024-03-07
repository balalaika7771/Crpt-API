package CrptAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Класс для работы с API Честного знака.
 * Поддерживает ограничение на количество запросов к API в заданный интервал времени.
 */
public class CrptApi {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Semaphore semaphore; // Семафор для ограничения количества запросов
    private final OkHttpClient httpClient; // HTTP клиент для отправки запросов
    private final ObjectMapper objectMapper; // Объект для сериализации/десериализации JSON

    /**
     * Создает экземпляр класса CrptApi.
     *
     * @param timeUnit    промежуток времени для ограничения запросов
     * @param requestLimit максимальное количество запросов в указанный промежуток времени
     */
    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        semaphore = new Semaphore(requestLimit);
        httpClient = new OkHttpClient();
        objectMapper = new ObjectMapper();

        // Запускаем задачу по распределению токенов семафора в конце каждого временного промежутка
        scheduler.scheduleAtFixedRate(this::replenishSemaphore, 0, 1, timeUnit);
    }

    /**
     * Создает документ для ввода в оборот товара.
     *
     * @param document  объект, представляющий документ
     * @param signature строка с подписью документа
     */
    public void createDocument(Document document, String signature) {
        try {
            semaphore.acquire(); // Запрашиваем разрешение у семафора, блокируя при необходимости
            String requestBody = buildRequestBody(document, signature);
            Request request = buildRequest(requestBody);
            Call call = httpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    semaphore.release(); // Освобождаем разрешение у семафора в случае ошибки
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    semaphore.release(); // Освобождаем разрешение у семафора после получения ответа
                    if (!response.isSuccessful()) {
                        System.out.println("Запрос завершился неудачно с кодом: " + response.code());
                    } else {
                        System.out.println("Запрос успешно выполнен");
                    }
                    response.close();
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем прерванный статус
        }
    }

    // Метод для пополнения токенов семафора
    private void replenishSemaphore() {
        semaphore.release(semaphore.availablePermits()); // Пополняем все разрешения
    }

    // Метод для построения тела запроса в формате JSON
    private String buildRequestBody(Document document, String signature) {
        try {
            return objectMapper.writeValueAsString(new RequestBody(document, signature));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для построения HTTP запроса
    private Request buildRequest(String requestBody) {
        return new Request.Builder()
                .url("https://ismp.crpt.ru/api/v3/lk/documents/create")
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .build();
    }

    // Внутренний класс, представляющий тело запроса
    static class RequestBody {
        private final Document description;
        private final String signature;

        public RequestBody(Document description, String signature) {
            this.description = description;
            this.signature = signature;
        }

        public Document getDescription() {
            return description;
        }

        public String getSignature() {
            return signature;
        }

        // Используем метод create() у класса RequestBody.Builder для создания объекта RequestBody
        public static okhttp3.RequestBody create(final MediaType contentType, final String content) {
            return okhttp3.RequestBody.create(contentType, content);
        }
    }

    // Внутренний класс, представляющий документ
    public class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private List<Product> products;
        private String reg_date;
        private String reg_number;

        // Конструктор для инициализации полей
        public Document(Description description, String doc_id, String doc_status, String doc_type, boolean importRequest,
                        String owner_inn, String participant_inn, String producer_inn, String production_date,
                        String production_type, List<Product> products, String reg_date, String reg_number) {
            this.description = description;
            this.doc_id = doc_id;
            this.doc_status = doc_status;
            this.doc_type = doc_type;
            this.importRequest = importRequest;
            this.owner_inn = owner_inn;
            this.participant_inn = participant_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.production_type = production_type;
            this.products = products;
            this.reg_date = reg_date;
            this.reg_number = reg_number;
        }

        // Геттеры и сеттеры для полей класса

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public String getProduction_date() {
            return production_date;
        }

        public void setProduction_date(String production_date) {
            this.production_date = production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public String getReg_date() {
            return reg_date;
        }

        public void setReg_date(String reg_date) {
            this.reg_date = reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }

        // Внутренний класс, представляющий описание документа
        static class Description {
            private String participantInn;

            public Description(String participantInn) {
                this.participantInn = participantInn;
            }

            public String getParticipantInn() {
                return participantInn;
            }

            public void setParticipantInn(String participantInn) {
                this.participantInn = participantInn;
            }
        }

        // Внутренний класс, представляющий продукт
        static class Product {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;

            public Product(String certificate_document, String certificate_document_date, String certificate_document_number,
                           String owner_inn, String producer_inn, String production_date, String tnved_code,
                           String uit_code, String uitu_code) {
                this.certificate_document = certificate_document;
                this.certificate_document_date = certificate_document_date;
                this.certificate_document_number = certificate_document_number;
                this.owner_inn = owner_inn;
                this.producer_inn = producer_inn;
                this.production_date = production_date;
                this.tnved_code = tnved_code;
                this.uit_code = uit_code;
                this.uitu_code = uitu_code;
            }

            public String getCertificate_document() {
                return certificate_document;
            }

            public void setCertificate_document(String certificate_document) {
                this.certificate_document = certificate_document;
            }

            public String getCertificate_document_date() {
                return certificate_document_date;
            }

            public void setCertificate_document_date(String certificate_document_date) {
                this.certificate_document_date = certificate_document_date;
            }

            public String getCertificate_document_number() {
                return certificate_document_number;
            }

            public void setCertificate_document_number(String certificate_document_number) {
                this.certificate_document_number = certificate_document_number;
            }

            public String getOwner_inn() {
                return owner_inn;
            }

            public void setOwner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
            }

            public String getProducer_inn() {
                return producer_inn;
            }

            public void setProducer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
            }

            public String getProduction_date() {
                return production_date;
            }

            public void setProduction_date(String production_date) {
                this.production_date = production_date;
            }

            public String getTnved_code() {
                return tnved_code;
            }

            public void setTnved_code(String tnved_code) {
                this.tnved_code = tnved_code;
            }

            public String getUit_code() {
                return uit_code;
            }

            public void setUit_code(String uit_code) {
                this.uit_code = uit_code;
            }

            public String getUitu_code() {
                return uitu_code;
            }

            public void setUitu_code(String uitu_code) {
                this.uitu_code = uitu_code;
            }
        }
    }



}
