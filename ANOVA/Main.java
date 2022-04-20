import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Pattern;


public class Main extends Application {

    private static final DecimalFormat df4 = new DecimalFormat("0.0000");
    private static final DecimalFormat df2 = new DecimalFormat("0.00");
    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static int ROW;
    private static int COL;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        ScrollPane scrollPane = new ScrollPane(); // center
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        GridPane grid = new GridPane();
        scrollPane.setContent(grid);
        VBox vBox = new VBox();

        // elementi HBox
        TextField tfBrMjerenja = new TextField();
        tfBrMjerenja.setPromptText("Broj mjerenja");
        tfBrMjerenja.setFocusTraversable(false);
        TextField tfBrAlternativa = new TextField();
        tfBrAlternativa.setPromptText("Broj alternativa");
        tfBrAlternativa.setFocusTraversable(false);

        Button buttonOK = new Button("OK");
        buttonOK.setOnAction(e -> {
            Integer brMjerenja = Integer.valueOf(tfBrMjerenja.getText());
            ROW = brMjerenja;
            Integer brAlternativa = Integer.valueOf(tfBrAlternativa.getText());
            COL = brAlternativa;
            grid.getChildren().clear();
            vBox.getChildren().clear();
            drawMatrix(brMjerenja, brAlternativa, grid); // iscrtavanje matrice TextField-a
        });


        Button buttonCalculate = new Button("Izračunaj");
        buttonCalculate.setOnAction(e -> {
            vBox.getChildren().clear(); // očistimo prethodni rezultat

            if (ROW == 0 || COL == 0) // ako nismo postavili vrijednosti n i k
                return;

            if (grid.getChildren().stream().anyMatch(node -> ((TextField) node).getText().equals("")) ||
                    grid.getChildren().stream().anyMatch(node -> !pattern.matcher(((TextField) node).getText()).matches())) // ako neko od polja nije uneseno
                return;

            // izračunavamo tek kad smo popunili matricu
            // učitavanje unesenih vrijednosti iz GUI u matricu
            double[][] matrix = new double[ROW][COL];
            Node result = null;
            ObservableList<Node> childrens = grid.getChildren();
            for (Node node : childrens) {
                int row = GridPane.getRowIndex(node);
                int col = GridPane.getColumnIndex(node);
                matrix[row][col] = Double.parseDouble(((TextField) node).getText());
            }

            Anova a = new Anova(ROW, COL, matrix);

            Label lblSSA = new Label("SSA: " + df4.format(a.getSSA()));
            Label lblSSE = new Label("SSE: " + df4.format(a.getSSE()));
            Label lblSST = new Label("SST: " + df4.format(a.getSST()));
            Label lblDegA = new Label("Stepen slobode [A]: " + a.getDegFreedomAlternatives());
            Label lblDegE = new Label("Stepen slobode [E]: " + a.getDegFreedomErrors());
            Label lblDegT = new Label("Stepen slobode [T]: " + a.getDegFreedomTotal());
            Label lblSqrA = new Label("Varijansa sume kvadrata [A]: " + df4.format(a.getMeanSquareAlternatives()));
            Label lblSqrE = new Label("Varijansa sume kvadrata [E]: " + df4.format(a.getMeanSquareErrors()));
            Label lblF = new Label("F izračunato: " + df2.format(a.getComputedF()));
            Label lblFtable = new Label("F tabelarno: " + df2.format(a.getTableF()));
            Label lblResult;
            if (a.getComputedF() > a.getTableF())
                lblResult = new Label("Razlike između alternativa su statistički značajne.");
            else
                lblResult = new Label("Razlike između alternativa nisu statistički značajne.");

            vBox.getChildren().addAll(lblSSA, lblSSE, lblSST, lblDegA, lblDegE, lblDegT, lblSqrA, lblSqrE, lblF, lblFtable, lblResult);

            for (String conResult : a.contrastResults) {
                vBox.getChildren().add(new Label(conResult));
            }
            vBox.setAlignment(Pos.TOP_LEFT);
            vBox.setSpacing(10);
            vBox.setPrefHeight(Region.USE_PREF_SIZE);
            vBox.setMaxWidth(Region.USE_PREF_SIZE);
            root.setRight(vBox);
        });

        HBox hbox = new HBox(tfBrMjerenja, tfBrAlternativa, buttonOK);
        HBox.setMargin(tfBrMjerenja, new

                Insets(10, 10, 10, 10));
        HBox.setMargin(tfBrAlternativa, new

                Insets(10, 10, 10, 10));
        hbox.setAlignment(Pos.CENTER);

        root.setTop(hbox);
        root.setCenter(scrollPane);
        root.setBottom(buttonCalculate);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(2);
        grid.setVgap(2);

        BorderPane.setAlignment(hbox, Pos.CENTER);
        BorderPane.setAlignment(scrollPane, Pos.CENTER);
        BorderPane.setAlignment(buttonCalculate, Pos.CENTER);
        BorderPane.setAlignment(vBox, Pos.CENTER_LEFT);
        BorderPane.setMargin(grid, new

                Insets(10, 10, 10, 10));
        BorderPane.setMargin(buttonCalculate, new

                Insets(10, 10, 10, 10));
        BorderPane.setMargin(vBox, new

                Insets(0, 10, 10, 10));
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("ANOVA & contrasts");
        String path = ".\\ANOVA\\A.png";
        Image applicationIcon = new Image(new File(path).toURI().toString());
        primaryStage.getIcons().add(applicationIcon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawMatrix(Integer brMjerenja, Integer brAlternativa, GridPane grid) {
        for (int x = 0; x < brMjerenja; x++) {
            for (int y = 0; y < brAlternativa; y++) {

                // Create a new TextField in each Iteration
                TextField tf = new TextField();
                tf.setMaxWidth(70);
                tf.setMaxWidth(20);
                tf.setMinWidth(70);
                tf.setMinHeight(20);

                tf.setAlignment(Pos.CENTER);
                tf.setPromptText(String.format("[%d,%d]", x, y));
                // Iterate the Index using the loops
                GridPane.setRowIndex(tf, x);
                GridPane.setColumnIndex(tf, y);
                grid.getChildren().add(tf);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
