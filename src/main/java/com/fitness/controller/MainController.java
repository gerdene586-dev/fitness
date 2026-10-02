package com.fitness.controller;

import com.fitness.model.Attendance;
import com.fitness.model.ClassBooking;
import com.fitness.model.Coach;
import com.fitness.model.Member;
import com.fitness.model.Membership;
import com.fitness.model.Payment;
import com.fitness.model.WorkoutClass;
import com.fitness.service.FitnessService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainController {

    public StackPane contentArea;

    private final FitnessService service = new FitnessService();

    private final ObservableList<Member> memberData =
            FXCollections.observableArrayList();

    private final ObservableList<Membership> membershipData =
            FXCollections.observableArrayList();

    private final ObservableList<Coach> coachData =
            FXCollections.observableArrayList();

    private final ObservableList<WorkoutClass> classData =
            FXCollections.observableArrayList();

    private final ObservableList<ClassBooking> bookingData =
            FXCollections.observableArrayList();

    private final ObservableList<Payment> paymentData =
            FXCollections.observableArrayList();

    private final ObservableList<Attendance> attendanceData =
            FXCollections.observableArrayList();

    // ==========================================================
    // ЭХЛЭЛ
    // ==========================================================

    public void initialize() {
        showDashboard();
    }

    // ==========================================================
    // ЕРӨНХИЙ PAGE
    // ==========================================================

    private VBox createPage(String title, String description) {

        VBox root = new VBox(10);
        root.setPadding(new Insets(30));
        root.setFillWidth(true);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("page-title");

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("page-description");

        root.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return root;
    }

    private void setContent(Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    // ==========================================================
    // DASHBOARD
    // ==========================================================

    public void showDashboard() {

        VBox root = createPage(
                "Хяналтын самбар",
                "Фитнесс төвийн үндсэн үзүүлэлтүүд"
        );

        HBox cards = new HBox(15);
        cards.setPadding(new Insets(20, 0, 20, 0));

        cards.getChildren().addAll(
                createCard(
                        "Нийт гишүүн",
                        String.valueOf(service.memberCount())
                ),
                createCard(
                        "Идэвхтэй гишүүнчлэл",
                        String.valueOf(service.activeMembershipCount())
                ),
                createCard(
                        "Дасгалжуулагч",
                        String.valueOf(service.coachCount())
                ),
                createCard(
                        "Хичээл",
                        String.valueOf(service.classCount())
                ),
                createCard(
                        "Бүртгэл",
                        String.valueOf(service.bookingCount())
                ),
                createCard(
                        "Орлого",
                        String.format(
                                "%,.0f ₮",
                                service.totalRevenue()
                        )
                )
        );

        Label info = new Label(
                "Өгөгдлийг MySQL database-д хадгалж байна."
        );

        info.getStyleClass().add("status-bar");

        root.getChildren().addAll(
                cards,
                info
        );

        setContent(root);

        System.out.println("Хяналтын самбар нээгдлээ.");
    }

    private VBox createCard(String title, String value) {

        VBox card = new VBox(8);

        card.setPrefWidth(180);
        card.getStyleClass().add("card");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        card.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return card;
    }

    // ==========================================================
    // ГИШҮҮН
    // ==========================================================

    public void showMembers() {

        VBox root = createPage(
                "Гишүүд",
                "Фитнесс төвийн бүх гишүүдийн бүртгэл"
        );

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TextField search = new TextField();
        search.setPromptText("Нэр, утас, имэйлээр хайх...");
        search.getStyleClass().add("search-field");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button add = new Button("＋ Гишүүн нэмэх");
        add.getStyleClass().add("primary-button");

        Button edit = new Button("Засах");
        edit.getStyleClass().add("secondary-button");

        Button delete = new Button("Устгах");
        delete.getStyleClass().add("danger-button");

        TableView<Member> table = new TableView<>();

        TableColumn<Member, String> id =
                column("№", m -> String.valueOf(m.getId()));

        TableColumn<Member, String> name =
                column("Нэр", Member::getName);

        TableColumn<Member, String> phone =
                column("Утас", Member::getPhone);

        TableColumn<Member, String> email =
                column("Имэйл", Member::getEmail);

        TableColumn<Member, String> gender =
                column("Хүйс", Member::getGender);

        TableColumn<Member, String> date =
                column("Бүртгүүлсэн", Member::getRegistrationDate);

        TableColumn<Member, String> status =
                column("Төлөв", Member::getStatus);

        table.getColumns().addAll(
                id,
                name,
                phone,
                email,
                gender,
                date,
                status
        );

        refreshMembers(table, "");

        search.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        refreshMembers(table, newValue)
        );

        add.setOnAction(e ->
                showMemberDialog(null, () -> refreshMembers(table, search.getText()))
        );

        edit.setOnAction(e -> {

            Member selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Засах гишүүнээ сонгоно уу.");
                return;
            }

            showMemberDialog(
                    selected,
                    () -> refreshMembers(table, search.getText())
            );
        });

        delete.setOnAction(e -> {

            Member selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Устгах гишүүнээ сонгоно уу.");
                return;
            }

            if (confirm(
                    "Гишүүн устгах",
                    selected.getName() + " гишүүнийг устгах уу?"
            )) {

                service.deleteMember(selected.getId());

                refreshMembers(table, search.getText());

                System.out.println(
                        "Гишүүн устгагдлаа: " + selected.getName()
                );
            }
        });

        toolbar.getChildren().addAll(
                search,
                add,
                edit,
                delete
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void refreshMembers(
            TableView<Member> table,
            String keyword
    ) {

        String q = keyword == null
                ? ""
                : keyword.toLowerCase().trim();

        List<Member> list = service.getMembers()
                .stream()
                .filter(m ->
                        m.getName().toLowerCase().contains(q)
                                || m.getPhone().contains(q)
                                || m.getEmail().toLowerCase().contains(q)
                )
                .toList();

        table.setItems(
                FXCollections.observableArrayList(list)
        );
    }

    private void showMemberDialog(
            Member existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ гишүүн"
                        : "Гишүүний мэдээлэл засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane().getButtonTypes().addAll(
                save,
                ButtonType.CANCEL
        );

        GridPane grid = formGrid();

        TextField name = field();
        TextField phone = field();
        TextField email = field();

        ComboBox<String> gender = combo(
                "Эрэгтэй",
                "Эмэгтэй"
        );

        ComboBox<String> status = combo(
                "Идэвхтэй",
                "Идэвхгүй"
        );

        if (existing != null) {

            name.setText(existing.getName());
            phone.setText(existing.getPhone());
            email.setText(existing.getEmail());
            gender.setValue(existing.getGender());
            status.setValue(existing.getStatus());

        } else {

            gender.setValue("Эрэгтэй");
            status.setValue("Идэвхтэй");
        }

        addField(grid, 0, "Нэр", name);
        addField(grid, 1, "Утас", phone);
        addField(grid, 2, "Имэйл", email);
        addField(grid, 3, "Хүйс", gender);
        addField(grid, 4, "Төлөв", status);

        dialog.getDialogPane().setContent(grid);

        Node saveButton =
                dialog.getDialogPane().lookupButton(save);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    if (name.getText().isBlank()) {
                        showError("Нэр хоосон байж болохгүй.");
                        event.consume();
                        return;
                    }

                    if (!phone.getText().matches("\\d{8}")) {
                        showError(
                                "Утасны дугаар 8 оронтой тоо байна."
                        );
                        event.consume();
                        return;
                    }

                    if (!email.getText().matches(
                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                    )) {

                        showError("Имэйл хаяг буруу байна.");
                        event.consume();
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == save) {

                if (existing == null) {

                    service.addMember(
                            new Member(
                                    0,
                                    name.getText().trim(),
                                    phone.getText().trim(),
                                    email.getText().trim(),
                                    gender.getValue(),
                                    LocalDate.now().toString(),
                                    status.getValue()
                            )
                    );

                    System.out.println(
                            "Шинэ гишүүн бүртгэгдлээ."
                    );

                } else {

                    existing.setName(name.getText().trim());
                    existing.setPhone(phone.getText().trim());
                    existing.setEmail(email.getText().trim());
                    existing.setGender(gender.getValue());
                    existing.setStatus(status.getValue());

                    service.updateMember(existing);

                    System.out.println(
                            "Гишүүний мэдээлэл шинэчлэгдлээ."
                    );
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // ГИШҮҮНЧЛЭЛ
    // ==========================================================

    public void showMemberships() {

        VBox root = createPage(
                "Гишүүнчлэл",
                "Гишүүдийн гишүүнчлэлийн хугацаа болон төлөв"
        );

        HBox toolbar = new HBox(10);

        TextField search = new TextField();
        search.setPromptText("Гишүүний нэрээр хайх...");
        search.getStyleClass().add("search-field");
        HBox.setHgrow(search, Priority.ALWAYS);

        ComboBox<String> filter = combo(
                "Бүгд",
                "Идэвхтэй",
                "Идэвхгүй",
                "Хугацаа дууссан"
        );

        Button add = new Button("＋ Гишүүнчлэл нэмэх");
        add.getStyleClass().add("primary-button");

        Button edit = new Button("Засах");
        edit.getStyleClass().add("secondary-button");

        Button delete = new Button("Устгах");
        delete.getStyleClass().add("danger-button");

        TableView<Membership> table = new TableView<>();

        table.getColumns().addAll(
                column("№", m -> String.valueOf(m.getId())),
                column("Гишүүн",
                        m -> service.getMemberName(m.getMemberId())),
                column("Төрөл", Membership::getType),
                column("Эхлэх", Membership::getStartDate),
                column("Дуусах", Membership::getEndDate),
                column("Үнэ",
                        m -> String.format(
                                "%,.0f ₮",
                                m.getPrice()
                        )),
                column("Төлөв", Membership::getStatus)
        );

        Runnable refresh = () -> {

            String q = search.getText()
                    .toLowerCase()
                    .trim();

            String f = filter.getValue();

            List<Membership> list =
                    service.getMemberships()
                            .stream()
                            .filter(m ->
                                    service.getMemberName(
                                            m.getMemberId()
                                    )
                                            .toLowerCase()
                                            .contains(q)
                            )
                            .filter(m ->
                                    "Бүгд".equals(f)
                                            || m.getStatus().equals(f)
                            )
                            .toList();

            table.setItems(
                    FXCollections.observableArrayList(list)
            );
        };

        search.textProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        filter.valueProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        refresh.run();

        add.setOnAction(e ->
                showMembershipDialog(
                        null,
                        refresh
                )
        );

        edit.setOnAction(e -> {

            Membership selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Засах гишүүнчлэлээ сонгоно уу.");
                return;
            }

            showMembershipDialog(
                    selected,
                    refresh
            );
        });

        delete.setOnAction(e -> {

            Membership selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Устгах гишүүнчлэлээ сонгоно уу.");
                return;
            }

            if (confirm(
                    "Гишүүнчлэл устгах",
                    "Сонгосон гишүүнчлэлийг устгах уу?"
            )) {

                service.deleteMembership(
                        selected.getId()
                );

                refresh.run();
            }
        });

        toolbar.getChildren().addAll(
                search,
                filter,
                add,
                edit,
                delete
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void showMembershipDialog(
            Membership existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ гишүүнчлэл"
                        : "Гишүүнчлэл засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane().getButtonTypes().addAll(
                save,
                ButtonType.CANCEL
        );

        GridPane grid = formGrid();

        ComboBox<String> member =
                new ComboBox<>();

        for (Member m : service.getMembers()) {
            member.getItems().add(
                    m.getId() + " - " + m.getName()
            );
        }

        ComboBox<String> type = combo(
                "Стандарт",
                "Премиум",
                "Оюутны",
                "Сарын"
        );

        TextField start = field();
        TextField end = field();
        TextField price = field();

        ComboBox<String> status = combo(
                "Идэвхтэй",
                "Идэвхгүй",
                "Хугацаа дууссан"
        );

        if (existing != null) {

            member.getSelectionModel().select(
                    findMemberText(existing.getMemberId())
            );

            type.setValue(existing.getType());
            start.setText(existing.getStartDate());
            end.setText(existing.getEndDate());
            price.setText(String.valueOf(existing.getPrice()));
            status.setValue(existing.getStatus());

        } else {

            if (!member.getItems().isEmpty()) {
                member.getSelectionModel().selectFirst();
            }

            type.setValue("Стандарт");
            start.setText(LocalDate.now().toString());
            end.setText(
                    LocalDate.now().plusMonths(1).toString()
            );
            status.setValue("Идэвхтэй");
        }

        addField(grid, 0, "Гишүүн", member);
        addField(grid, 1, "Төрөл", type);
        addField(grid, 2, "Эхлэх огноо", start);
        addField(grid, 3, "Дуусах огноо", end);
        addField(grid, 4, "Үнэ", price);
        addField(grid, 5, "Төлөв", status);

        dialog.getDialogPane().setContent(grid);

        Node saveButton =
                dialog.getDialogPane().lookupButton(save);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    if (member.getValue() == null) {
                        showError("Гишүүн сонгоно уу.");
                        event.consume();
                        return;
                    }

                    if (start.getText().isBlank()
                            || end.getText().isBlank()) {

                        showError("Огноо оруулна уу.");
                        event.consume();
                        return;
                    }

                    try {
                        double value =
                                Double.parseDouble(
                                        price.getText()
                                );

                        if (value <= 0) {
                            throw new NumberFormatException();
                        }

                    } catch (NumberFormatException ex) {

                        showError(
                                "Үнэ зөв тоо байна."
                        );

                        event.consume();
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == save) {

                long memberId =
                        Long.parseLong(
                                member.getValue()
                                        .split(" - ")[0]
                        );

                double amount =
                        Double.parseDouble(
                                price.getText()
                        );

                if (existing == null) {

                    service.addMembership(
                            new Membership(
                                    0,
                                    memberId,
                                    type.getValue(),
                                    start.getText(),
                                    end.getText(),
                                    amount,
                                    status.getValue()
                            )
                    );

                } else {

                    existing.setMemberId(memberId);
                    existing.setType(type.getValue());
                    existing.setStartDate(start.getText());
                    existing.setEndDate(end.getText());
                    existing.setPrice(amount);
                    existing.setStatus(status.getValue());

                    service.updateMembership(existing);
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // ДАСГАЛЖУУЛАГЧ
    // ==========================================================

    public void showCoaches() {

        VBox root = createPage(
                "Дасгалжуулагч",
                "Дасгалжуулагчдын бүртгэл"
        );

        HBox toolbar = new HBox(10);

        TextField search = new TextField();
        search.setPromptText(
                "Дасгалжуулагч хайх..."
        );
        search.getStyleClass().add("search-field");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button add = new Button("＋ Дасгалжуулагч нэмэх");
        add.getStyleClass().add("primary-button");

        Button edit = new Button("Засах");
        edit.getStyleClass().add("secondary-button");

        Button delete = new Button("Устгах");
        delete.getStyleClass().add("danger-button");

        TableView<Coach> table = new TableView<>();

        table.getColumns().addAll(
                column("№", c -> String.valueOf(c.getId())),
                column("Нэр", Coach::getName),
                column("Утас", Coach::getPhone),
                column("Мэргэжил",
                        Coach::getSpecialization),
                column("Төлөв", Coach::getStatus)
        );

        Runnable refresh = () -> {

            String q = search.getText()
                    .toLowerCase()
                    .trim();

            table.setItems(
                    FXCollections.observableArrayList(
                            service.getCoaches()
                                    .stream()
                                    .filter(c ->
                                            c.getName()
                                                    .toLowerCase()
                                                    .contains(q)
                                                    ||
                                            c.getSpecialization()
                                                    .toLowerCase()
                                                    .contains(q)
                                    )
                                    .toList()
                    )
            );
        };

        search.textProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        refresh.run();

        add.setOnAction(e ->
                showCoachDialog(null, refresh)
        );

        edit.setOnAction(e -> {

            Coach selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError(
                        "Засах дасгалжуулагчаа сонгоно уу."
                );
                return;
            }

            showCoachDialog(
                    selected,
                    refresh
            );
        });

        delete.setOnAction(e -> {

            Coach selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError(
                        "Устгах дасгалжуулагчаа сонгоно уу."
                );
                return;
            }

            if (confirm(
                    "Дасгалжуулагч устгах",
                    "Сонгосон дасгалжуулагчийг устгах уу?"
            )) {

                service.deleteCoach(
                        selected.getId()
                );

                refresh.run();
            }
        });

        toolbar.getChildren().addAll(
                search,
                add,
                edit,
                delete
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void showCoachDialog(
            Coach existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ дасгалжуулагч"
                        : "Дасгалжуулагч засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane().getButtonTypes().addAll(
                save,
                ButtonType.CANCEL
        );

        GridPane grid = formGrid();

        TextField name = field();
        TextField phone = field();
        TextField specialization = field();

        ComboBox<String> status =
                combo(
                        "Идэвхтэй",
                        "Идэвхгүй"
                );

        if (existing != null) {

            name.setText(existing.getName());
            phone.setText(existing.getPhone());
            specialization.setText(
                    existing.getSpecialization()
            );
            status.setValue(existing.getStatus());

        } else {

            status.setValue("Идэвхтэй");
        }

        addField(grid, 0, "Нэр", name);
        addField(grid, 1, "Утас", phone);
        addField(grid, 2, "Мэргэжил", specialization);
        addField(grid, 3, "Төлөв", status);

        dialog.getDialogPane().setContent(grid);

        Node saveButton =
                dialog.getDialogPane().lookupButton(save);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    if (name.getText().isBlank()
                            || specialization.getText().isBlank()) {

                        showError(
                                "Нэр болон мэргэжил заавал оруулна."
                        );

                        event.consume();
                        return;
                    }

                    if (!phone.getText().matches("\\d{8}")) {

                        showError(
                                "Утас 8 оронтой тоо байна."
                        );

                        event.consume();
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == save) {

                if (existing == null) {

                    service.addCoach(
                            new Coach(
                                    0,
                                    name.getText().trim(),
                                    phone.getText().trim(),
                                    specialization.getText().trim(),
                                    status.getValue()
                            )
                    );

                } else {

                    existing.setName(name.getText().trim());
                    existing.setPhone(phone.getText().trim());
                    existing.setSpecialization(
                            specialization.getText().trim()
                    );
                    existing.setStatus(
                            status.getValue()
                    );

                    service.updateCoach(existing);
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // ХИЧЭЭЛ
    // ==========================================================

    public void showClasses() {

        VBox root = createPage(
                "Хичээлийн хуваарь",
                "Фитнессийн хичээлүүдийн хуваарь"
        );

        HBox toolbar = new HBox(10);

        TextField search = new TextField();
        search.setPromptText("Хичээл хайх...");
        search.getStyleClass().add("search-field");
        HBox.setHgrow(search, Priority.ALWAYS);

        Button add = new Button("＋ Хичээл нэмэх");
        add.getStyleClass().add("primary-button");

        Button edit = new Button("Засах");
        edit.getStyleClass().add("secondary-button");

        Button delete = new Button("Устгах");
        delete.getStyleClass().add("danger-button");

        TableView<WorkoutClass> table =
                new TableView<>();

        table.getColumns().addAll(
                column("№",
                        c -> String.valueOf(c.getId())),
                column("Хичээл",
                        WorkoutClass::getName),
                column("Дасгалжуулагч",
                        c -> service.getCoachName(
                                c.getCoachId()
                        )),
                column("Огноо",
                        WorkoutClass::getDate),
                column("Цаг",
                        WorkoutClass::getTime),
                column("Танхим",
                        WorkoutClass::getRoom),
                column("Багтаамж",
                        c -> String.valueOf(
                                c.getCapacity()
                        )),
                column("Төлөв",
                        WorkoutClass::getStatus)
        );

        Runnable refresh = () -> {

            String q = search.getText()
                    .toLowerCase()
                    .trim();

            table.setItems(
                    FXCollections.observableArrayList(
                            service.getClasses()
                                    .stream()
                                    .filter(c ->
                                            c.getName()
                                                    .toLowerCase()
                                                    .contains(q)
                                    )
                                    .toList()
                    )
            );
        };

        search.textProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        refresh.run();

        add.setOnAction(e ->
                showClassDialog(null, refresh)
        );

        edit.setOnAction(e -> {

            WorkoutClass selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError("Засах хичээлээ сонгоно уу.");
                return;
            }

            showClassDialog(
                    selected,
                    refresh
            );
        });

        delete.setOnAction(e -> {

            WorkoutClass selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError("Устгах хичээлээ сонгоно уу.");
                return;
            }

            if (confirm(
                    "Хичээл устгах",
                    "Сонгосон хичээлийг устгах уу?"
            )) {

                service.deleteClass(
                        selected.getId()
                );

                refresh.run();
            }
        });

        toolbar.getChildren().addAll(
                search,
                add,
                edit,
                delete
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void showClassDialog(
            WorkoutClass existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ хичээл"
                        : "Хичээл засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        save,
                        ButtonType.CANCEL
                );

        GridPane grid = formGrid();

        TextField name = field();

        ComboBox<String> coach =
                new ComboBox<>();

        for (Coach c : service.getCoaches()) {

            coach.getItems().add(
                    c.getId() + " - " + c.getName()
            );
        }

        TextField date = field();
        TextField time = field();
        TextField room = field();
        TextField capacity = field();

        ComboBox<String> status =
                combo(
                        "Идэвхтэй",
                        "Идэвхгүй"
                );

        if (existing != null) {

            name.setText(existing.getName());

            coach.getSelectionModel()
                    .select(
                            findCoachText(
                                    existing.getCoachId()
                            )
                    );

            date.setText(existing.getDate());
            time.setText(existing.getTime());
            room.setText(existing.getRoom());
            capacity.setText(
                    String.valueOf(
                            existing.getCapacity()
                    )
            );
            status.setValue(existing.getStatus());

        } else {

            if (!coach.getItems().isEmpty()) {
                coach.getSelectionModel().selectFirst();
            }

            date.setText(
                    LocalDate.now().toString()
            );

            time.setText("10:00");
            capacity.setText("20");
            status.setValue("Идэвхтэй");
        }

        addField(grid, 0, "Хичээлийн нэр", name);
        addField(grid, 1, "Дасгалжуулагч", coach);
        addField(grid, 2, "Огноо", date);
        addField(grid, 3, "Цаг", time);
        addField(grid, 4, "Танхим", room);
        addField(grid, 5, "Багтаамж", capacity);
        addField(grid, 6, "Төлөв", status);

        dialog.getDialogPane()
                .setContent(grid);

        Node saveButton =
                dialog.getDialogPane()
                        .lookupButton(save);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    if (name.getText().isBlank()
                            || coach.getValue() == null
                            || room.getText().isBlank()) {

                        showError(
                                "Шаардлагатай мэдээллийг бүрэн оруулна уу."
                        );

                        event.consume();
                        return;
                    }

                    try {

                        int cap =
                                Integer.parseInt(
                                        capacity.getText()
                                );

                        if (cap <= 0) {
                            throw new NumberFormatException();
                        }

                    } catch (NumberFormatException ex) {

                        showError(
                                "Багтаамж зөв тоо байна."
                        );

                        event.consume();
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == save) {

                long coachId =
                        Long.parseLong(
                                coach.getValue()
                                        .split(" - ")[0]
                        );

                int cap =
                        Integer.parseInt(
                                capacity.getText()
                        );

                if (existing == null) {

                    service.addClass(
                            new WorkoutClass(
                                    0,
                                    name.getText().trim(),
                                    coachId,
                                    date.getText().trim(),
                                    time.getText().trim(),
                                    room.getText().trim(),
                                    cap,
                                    status.getValue()
                            )
                    );

                } else {

                    existing.setName(
                            name.getText().trim()
                    );

                    existing.setCoachId(coachId);

                    existing.setDate(
                            date.getText().trim()
                    );

                    existing.setTime(
                            time.getText().trim()
                    );

                    existing.setRoom(
                            room.getText().trim()
                    );

                    existing.setCapacity(cap);

                    existing.setStatus(
                            status.getValue()
                    );

                    service.updateClass(existing);
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // BOOKING
    // ==========================================================

    public void showBookings() {

        VBox root = createPage(
                "Бүртгэл",
                "Гишүүдийн хичээлд бүртгүүлсэн мэдээлэл"
        );

        HBox toolbar = new HBox(10);

        TextField search = new TextField();
        search.setPromptText(
                "Гишүүн эсвэл хичээлээр хайх..."
        );
        search.getStyleClass().add("search-field");
        HBox.setHgrow(search, Priority.ALWAYS);

        ComboBox<String> filter =
                combo(
                        "Бүгд",
                        "Баталгаажсан",
                        "Цуцалсан"
                );

        Button add =
                new Button("＋ Бүртгэл нэмэх");

        add.getStyleClass()
                .add("primary-button");

        Button edit =
                new Button("Засах");

        edit.getStyleClass()
                .add("secondary-button");

        Button delete =
                new Button("Устгах");

        delete.getStyleClass()
                .add("danger-button");

        TableView<ClassBooking> table =
                new TableView<>();

        table.getColumns().addAll(
                column("№",
                        b -> String.valueOf(
                                b.getId()
                        )),
                column("Гишүүн",
                        b -> service.getMemberName(
                                b.getMemberId()
                        )),
                column("Хичээл",
                        b -> service.getClassName(
                                b.getClassId()
                        )),
                column("Бүртгэсэн огноо",
                        ClassBooking::getBookingDate),
                column("Төлөв",
                        ClassBooking::getStatus)
        );

        Runnable refresh = () -> {

            String q =
                    search.getText()
                            .toLowerCase()
                            .trim();

            String f = filter.getValue();

            table.setItems(
                    FXCollections.observableArrayList(
                            service.getBookings()
                                    .stream()
                                    .filter(b -> {

                                        String member =
                                                service.getMemberName(
                                                        b.getMemberId()
                                                );

                                        String clazz =
                                                service.getClassName(
                                                        b.getClassId()
                                                );

                                        return member
                                                .toLowerCase()
                                                .contains(q)
                                                ||
                                                clazz
                                                        .toLowerCase()
                                                        .contains(q);
                                    })
                                    .filter(b ->
                                            "Бүгд".equals(f)
                                                    ||
                                                    b.getStatus()
                                                            .equals(f)
                                    )
                                    .toList()
                    )
            );
        };

        search.textProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        filter.valueProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        refresh.run();

        add.setOnAction(e ->
                showBookingDialog(
                        null,
                        refresh
                )
        );

        edit.setOnAction(e -> {

            ClassBooking selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError(
                        "Засах бүртгэлээ сонгоно уу."
                );
                return;
            }

            showBookingDialog(
                    selected,
                    refresh
            );
        });

        delete.setOnAction(e -> {

            ClassBooking selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError(
                        "Устгах бүртгэлээ сонгоно уу."
                );
                return;
            }

            if (confirm(
                    "Бүртгэл устгах",
                    "Сонгосон бүртгэлийг устгах уу?"
            )) {

                service.deleteBooking(
                        selected.getId()
                );

                refresh.run();
            }
        });

        toolbar.getChildren().addAll(
                search,
                filter,
                add,
                edit,
                delete
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void showBookingDialog(
            ClassBooking existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ бүртгэл"
                        : "Бүртгэл засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        save,
                        ButtonType.CANCEL
                );

        GridPane grid = formGrid();

        ComboBox<String> member =
                new ComboBox<>();

        for (Member m :
                service.getMembers()) {

            member.getItems().add(
                    m.getId() + " - " + m.getName()
            );
        }

        ComboBox<String> clazz =
                new ComboBox<>();

        for (WorkoutClass c :
                service.getClasses()) {

            clazz.getItems().add(
                    c.getId() + " - " + c.getName()
            );
        }

        TextField date = field();

        ComboBox<String> status =
                combo(
                        "Баталгаажсан",
                        "Цуцалсан"
                );

        if (existing != null) {

            member.getSelectionModel()
                    .select(
                            findMemberText(
                                    existing.getMemberId()
                            )
                    );

            clazz.getSelectionModel()
                    .select(
                            findClassText(
                                    existing.getClassId()
                            )
                    );

            date.setText(
                    existing.getBookingDate()
            );

            status.setValue(
                    existing.getStatus()
            );

        } else {

            if (!member.getItems().isEmpty()) {
                member.getSelectionModel()
                        .selectFirst();
            }

            if (!clazz.getItems().isEmpty()) {
                clazz.getSelectionModel()
                        .selectFirst();
            }

            date.setText(
                    LocalDate.now().toString()
            );

            status.setValue("Баталгаажсан");
        }

        addField(grid, 0, "Гишүүн", member);
        addField(grid, 1, "Хичээл", clazz);
        addField(grid, 2, "Огноо", date);
        addField(grid, 3, "Төлөв", status);

        dialog.getDialogPane()
                .setContent(grid);

        Node saveButton =
                dialog.getDialogPane()
                        .lookupButton(save);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    if (member.getValue() == null
                            || clazz.getValue() == null) {

                        showError(
                                "Гишүүн болон хичээл сонгоно уу."
                        );

                        event.consume();
                        return;
                    }

                    long memberId =
                            Long.parseLong(
                                    member.getValue()
                                            .split(" - ")[0]
                            );

                    long classId =
                            Long.parseLong(
                                    clazz.getValue()
                                            .split(" - ")[0]
                            );

                    if (existing == null
                            && service.isAlreadyBooked(
                            memberId,
                            classId
                    )) {

                        showError(
                                "Энэ гишүүн уг хичээлд аль хэдийн бүртгүүлсэн байна."
                        );

                        event.consume();
                        return;
                    }

                    WorkoutClass workoutClass =
                            service.getClasses()
                                    .stream()
                                    .filter(c ->
                                            c.getId() == classId
                                    )
                                    .findFirst()
                                    .orElse(null);

                    if (existing == null
                            && workoutClass != null
                            && service.getBookingCountForClass(
                            classId
                    ) >= workoutClass.getCapacity()) {

                        showError(
                                "Энэ хичээлийн багтаамж дүүрсэн байна."
                        );

                        event.consume();
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == save) {

                long memberId =
                        Long.parseLong(
                                member.getValue()
                                        .split(" - ")[0]
                        );

                long classId =
                        Long.parseLong(
                                clazz.getValue()
                                        .split(" - ")[0]
                        );

                if (existing == null) {

                    service.addBooking(
                            new ClassBooking(
                                    0,
                                    memberId,
                                    classId,
                                    date.getText(),
                                    status.getValue()
                            )
                    );

                } else {

                    existing.setMemberId(memberId);
                    existing.setClassId(classId);
                    existing.setBookingDate(
                            date.getText()
                    );
                    existing.setStatus(
                            status.getValue()
                    );

                    service.updateBooking(existing);
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // ТӨЛБӨР
    // ==========================================================

    public void showPayments() {

        VBox root = createPage(
                "Төлбөр",
                "Гишүүдийн төлбөрийн бүртгэл"
        );

        HBox toolbar = new HBox(10);

        TextField search = new TextField();
        search.setPromptText(
                "Гишүүний нэрээр хайх..."
        );
        search.getStyleClass()
                .add("search-field");

        HBox.setHgrow(
                search,
                Priority.ALWAYS
        );

        Button add =
                new Button("＋ Төлбөр нэмэх");

        add.getStyleClass()
                .add("primary-button");

        Button edit =
                new Button("Засах");

        edit.getStyleClass()
                .add("secondary-button");

        Button delete =
                new Button("Устгах");

        delete.getStyleClass()
                .add("danger-button");

        TableView<Payment> table =
                new TableView<>();

        table.getColumns().addAll(
                column("№",
                        p -> String.valueOf(
                                p.getId()
                        )),
                column("Гишүүн",
                        p -> service.getMemberName(
                                p.getMemberId()
                        )),
                column("Дүн",
                        p -> String.format(
                                "%,.0f ₮",
                                p.getAmount()
                        )),
                column("Огноо",
                        Payment::getPaymentDate),
                column("Төлбөрийн хэлбэр",
                        Payment::getMethod),
                column("Төлөв",
                        Payment::getStatus),
                column("Тайлбар",
                        Payment::getDescription)
        );

        Runnable refresh = () -> {

            String q =
                    search.getText()
                            .toLowerCase()
                            .trim();

            table.setItems(
                    FXCollections.observableArrayList(
                            service.getPayments()
                                    .stream()
                                    .filter(p ->
                                            service.getMemberName(
                                                    p.getMemberId()
                                            )
                                                    .toLowerCase()
                                                    .contains(q)
                                    )
                                    .toList()
                    )
            );
        };

        search.textProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        refresh.run();

        add.setOnAction(e ->
                showPaymentDialog(
                        null,
                        refresh
                )
        );

        edit.setOnAction(e -> {

            Payment selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError(
                        "Засах төлбөрөө сонгоно уу."
                );
                return;
            }

            showPaymentDialog(
                    selected,
                    refresh
            );
        });

        delete.setOnAction(e -> {

            Payment selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError(
                        "Устгах төлбөрөө сонгоно уу."
                );
                return;
            }

            if (confirm(
                    "Төлбөр устгах",
                    "Сонгосон төлбөрийг устгах уу?"
            )) {

                service.deletePayment(
                        selected.getId()
                );

                refresh.run();
            }
        });

        toolbar.getChildren().addAll(
                search,
                add,
                edit,
                delete
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void showPaymentDialog(
            Payment existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ төлбөр"
                        : "Төлбөр засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        save,
                        ButtonType.CANCEL
                );

        GridPane grid =
                formGrid();

        ComboBox<String> member =
                new ComboBox<>();

        for (Member m :
                service.getMembers()) {

            member.getItems().add(
                    m.getId() + " - " + m.getName()
            );
        }

        TextField amount = field();
        TextField date = field();

        ComboBox<String> method =
                combo(
                        "Бэлэн",
                        "Дансаар",
                        "Карт"
                );

        ComboBox<String> status =
                combo(
                        "Төлөгдсөн",
                        "Хүлээгдэж буй",
                        "Буцаагдсан"
                );

        TextField description = field();

        if (existing != null) {

            member.getSelectionModel()
                    .select(
                            findMemberText(
                                    existing.getMemberId()
                            )
                    );

            amount.setText(
                    String.valueOf(
                            existing.getAmount()
                    )
            );

            date.setText(
                    existing.getPaymentDate()
            );

            method.setValue(
                    existing.getMethod()
            );

            status.setValue(
                    existing.getStatus()
            );

            description.setText(
                    existing.getDescription()
            );

        } else {

            if (!member.getItems().isEmpty()) {
                member.getSelectionModel()
                        .selectFirst();
            }

            date.setText(
                    LocalDate.now().toString()
            );

            method.setValue("Бэлэн");
            status.setValue("Төлөгдсөн");
        }

        addField(grid, 0, "Гишүүн", member);
        addField(grid, 1, "Дүн", amount);
        addField(grid, 2, "Огноо", date);
        addField(grid, 3, "Төлбөрийн хэлбэр", method);
        addField(grid, 4, "Төлөв", status);
        addField(grid, 5, "Тайлбар", description);

        dialog.getDialogPane()
                .setContent(grid);

        Node saveButton =
                dialog.getDialogPane()
                        .lookupButton(save);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    try {

                        double value =
                                Double.parseDouble(
                                        amount.getText()
                                );

                        if (value <= 0) {
                            throw new NumberFormatException();
                        }

                    } catch (NumberFormatException ex) {

                        showError(
                                "Төлбөрийн дүн зөв тоо байна."
                        );

                        event.consume();
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == save) {

                long memberId =
                        Long.parseLong(
                                member.getValue()
                                        .split(" - ")[0]
                        );

                double value =
                        Double.parseDouble(
                                amount.getText()
                        );

                if (existing == null) {

                    service.addPayment(
                            new Payment(
                                    0,
                                    memberId,
                                    value,
                                    date.getText(),
                                    method.getValue(),
                                    status.getValue(),
                                    description.getText()
                            )
                    );

                } else {

                    existing.setMemberId(memberId);
                    existing.setAmount(value);
                    existing.setPaymentDate(
                            date.getText()
                    );
                    existing.setMethod(
                            method.getValue()
                    );
                    existing.setStatus(
                            status.getValue()
                    );
                    existing.setDescription(
                            description.getText()
                    );

                    service.updatePayment(existing);
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // ИРЦ
    // ==========================================================

    public void showAttendance() {

        VBox root = createPage(
                "Ирц",
                "Хичээлд бүртгүүлсэн гишүүдийн ирц"
        );

        HBox toolbar = new HBox(10);

        ComboBox<String> filter =
                combo(
                        "Бүгд",
                        "Ирсэн",
                        "Ирээгүй",
                        "Хоцорсон"
                );

        Button add =
                new Button("＋ Ирц нэмэх");

        add.getStyleClass()
                .add("primary-button");

        Button edit =
                new Button("Төлөв засах");

        edit.getStyleClass()
                .add("secondary-button");

        Button delete =
                new Button("Устгах");

        delete.getStyleClass()
                .add("danger-button");

        TableView<Attendance> table =
                new TableView<>();

        table.getColumns().addAll(
                column("№",
                        a -> String.valueOf(
                                a.getId()
                        )),
                column("Гишүүн",
                        a -> service.getBookingMemberName(
                                a.getBookingId()
                        )),
                column("Огноо",
                        Attendance::getDate),
                column("Ирц",
                        Attendance::getStatus)
        );

        Runnable refresh = () -> {

            String f =
                    filter.getValue();

            table.setItems(
                    FXCollections.observableArrayList(
                            service.getAttendances()
                                    .stream()
                                    .filter(a ->
                                            "Бүгд".equals(f)
                                                    ||
                                            a.getStatus()
                                                    .equals(f)
                                    )
                                    .toList()
                    )
            );
        };

        filter.valueProperty().addListener(
                (obs, o, n) -> refresh.run()
        );

        refresh.run();

        add.setOnAction(e ->
                showAttendanceDialog(
                        null,
                        refresh
                )
        );

        edit.setOnAction(e -> {

            Attendance selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError(
                        "Засах ирцээ сонгоно уу."
                );
                return;
            }

            showAttendanceDialog(
                    selected,
                    refresh
            );
        });

        delete.setOnAction(e -> {

            Attendance selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {
                showError(
                        "Устгах ирцээ сонгоно уу."
                );
                return;
            }

            if (confirm(
                    "Ирц устгах",
                    "Сонгосон ирцийг устгах уу?"
            )) {

                service.deleteAttendance(
                        selected.getId()
                );

                refresh.run();
            }
        });

        toolbar.getChildren().addAll(
                filter,
                add,
                edit,
                delete
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        root.getChildren().addAll(
                toolbar,
                table
        );

        setContent(root);
    }

    private void showAttendanceDialog(
            Attendance existing,
            Runnable refresh
    ) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                existing == null
                        ? "Шинэ ирц"
                        : "Ирц засах"
        );

        ButtonType save =
                new ButtonType(
                        "Хадгалах",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        save,
                        ButtonType.CANCEL
                );

        GridPane grid =
                formGrid();

        ComboBox<String> booking =
                new ComboBox<>();

        for (ClassBooking b :
                service.getBookings()) {

            booking.getItems().add(
                    b.getId()
                            + " - "
                            + service.getMemberName(
                            b.getMemberId()
                    )
                            + " / "
                            + service.getClassName(
                            b.getClassId()
                    )
            );
        }

        TextField date = field();

        ComboBox<String> status =
                combo(
                        "Ирсэн",
                        "Ирээгүй",
                        "Хоцорсон"
                );

        if (existing != null) {

            booking.getSelectionModel()
                    .select(
                            findBookingText(
                                    existing.getBookingId()
                            )
                    );

            date.setText(
                    existing.getDate()
            );

            status.setValue(
                    existing.getStatus()
            );

        } else {

            if (!booking.getItems().isEmpty()) {
                booking.getSelectionModel()
                        .selectFirst();
            }

            date.setText(
                    LocalDate.now().toString()
            );

            status.setValue("Ирсэн");
        }

        addField(
                grid,
                0,
                "Бүртгэл",
                booking
        );

        addField(
                grid,
                1,
                "Огноо",
                date
        );

        addField(
                grid,
                2,
                "Ирц",
                status
        );

        dialog.getDialogPane()
                .setContent(grid);

        dialog.setResultConverter(button -> {

            if (button == save) {

                long bookingId =
                        Long.parseLong(
                                booking.getValue()
                                        .split(" - ")[0]
                        );

                if (existing == null) {

                    service.addAttendance(
                            new Attendance(
                                    0,
                                    bookingId,
                                    date.getText(),
                                    status.getValue()
                            )
                    );

                } else {

                    existing.setBookingId(
                            bookingId
                    );

                    existing.setDate(
                            date.getText()
                    );

                    existing.setStatus(
                            status.getValue()
                    );

                    service.updateAttendance(
                            existing
                    );
                }

                refresh.run();
            }

            return button;
        });

        dialog.showAndWait();
    }

    // ==========================================================
    // NAVIGATION
    // ==========================================================

    public void showDashboardFromMenu() {
        showDashboard();
    }

    // ==========================================================
    // ТУСЛАХ МЕТОД
    // ==========================================================

    private <T> TableColumn<T, String> column(
            String title,
            Function<T, String> mapper
    ) {

        TableColumn<T, String> column =
                new TableColumn<>(title);

        column.setCellValueFactory(
                cell ->
                        new SimpleStringProperty(
                                mapper.apply(
                                        cell.getValue()
                                )
                        )
        );

        column.setPrefWidth(140);

        return column;
    }

    private TextField field() {

        TextField field =
                new TextField();

        field.setPrefWidth(300);

        return field;
    }

    private ComboBox<String> combo(
            String... values
    ) {

        ComboBox<String> box =
                new ComboBox<>();

        box.getItems().addAll(values);

        box.setPrefWidth(300);

        return box;
    }

    private GridPane formGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);

        grid.setPadding(
                new Insets(20)
        );

        return grid;
    }

    private void addField(
            GridPane grid,
            int row,
            String label,
            Node node
    ) {

        Label text =
                new Label(label);

        text.getStyleClass()
                .add("form-label");

        grid.add(
                text,
                0,
                row
        );

        grid.add(
                node,
                1,
                row
        );
    }

    private String findMemberText(
            long id
    ) {

        return service.getMembers()
                .stream()
                .filter(m -> m.getId() == id)
                .map(m ->
                        m.getId()
                                + " - "
                                + m.getName()
                )
                .findFirst()
                .orElse(null);
    }

    private String findCoachText(
            long id
    ) {

        return service.getCoaches()
                .stream()
                .filter(c -> c.getId() == id)
                .map(c ->
                        c.getId()
                                + " - "
                                + c.getName()
                )
                .findFirst()
                .orElse(null);
    }

    private String findClassText(
            long id
    ) {

        return service.getClasses()
                .stream()
                .filter(c -> c.getId() == id)
                .map(c ->
                        c.getId()
                                + " - "
                                + c.getName()
                )
                .findFirst()
                .orElse(null);
    }

    private String findBookingText(
            long id
    ) {

        return service.getBookings()
                .stream()
                .filter(b -> b.getId() == id)
                .map(b ->
                        b.getId()
                                + " - "
                                + service.getMemberName(
                                b.getMemberId()
                        )
                                + " / "
                                + service.getClassName(
                                b.getClassId()
                        )
                )
                .findFirst()
                .orElse(null);
    }

    private void showError(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Алдаа");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();

        System.out.println(
                "Алдаа: " + message
        );
    }

    private boolean confirm(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait()
                .filter(
                        ButtonType.OK::equals
                )
                .isPresent();
    }
}