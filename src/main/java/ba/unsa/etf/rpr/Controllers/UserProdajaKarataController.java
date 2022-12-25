package ba.unsa.etf.rpr.Controllers;

import ba.unsa.etf.rpr.App;
import ba.unsa.etf.rpr.dao.*;
import ba.unsa.etf.rpr.domain.Film;
import ba.unsa.etf.rpr.domain.Karta;
import ba.unsa.etf.rpr.exceptions.FilmoviException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static ba.unsa.etf.rpr.Controllers.LoginController.user;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class UserProdajaKarataController {
    public TextField brojKarataTextField;
    public Label zanrLabel;
    public Label trajanjeLabel;
    public Label cijenaLabel;
    public Label zanrLabelFiksna;
    public Label trajanjeLabelFiksna;
    public Label cijenaLabelFiksna;
    private final List<String> listaFilmova = DaoFactory.filmDao().getAllNames();
    private final ObservableList<String> filmovi = FXCollections.observableArrayList(listaFilmova);
    public ChoiceBox<String> filmChoiceBox;
    private String imeOdabranogFilma = "";
    private int brojKarata = 0;
    public DatePicker odabirDatuma;
    private LocalDate datum;
    public Button kupiButton;
    private Film film = new Film();
    private int ukupnaCijena;

    public UserProdajaKarataController() throws FilmoviException {
    }

    @FXML
    private void initialize() {
        filmChoiceBox.setItems(filmovi);
        filmChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                try {
                    imeOdabranogFilma = listaFilmova.get(newValue.intValue());
                    Film f = DaoFactory.filmDao().getByIme(imeOdabranogFilma);
                    trajanjeLabelFiksna.setText("TRAJANJE:");
                    cijenaLabelFiksna.setText("CIJENA:");
                    zanrLabelFiksna.setText("ZANR:");
                    trajanjeLabel.setText(f.getTrajanje() + " MIN");
                    zanrLabel.setText(f.getZanr());
                    cijenaLabel.setText(f.getCijena() + " KM");
                } catch (FilmoviException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void odabirDatumaClick(ActionEvent actionEvent) {
        datum = odabirDatuma.getValue();
    }

    public void kupiButtonClick(ActionEvent actionEvent) throws IOException, FilmoviException {
        try {
            brojKarata = Integer.parseInt(brojKarataTextField.getText());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Pogresni podaci");
            alert.setContentText("Uneseni su nevalidni podaci!");
            alert.showAndWait();
        }
        ukupnaCijena = brojKarata * DaoFactory.filmDao().getByIme(imeOdabranogFilma).getCijena();
        if (imeOdabranogFilma.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Odaberite film!");
            alert.setContentText("Niti jedan film nije odabran.");
            alert.showAndWait();
            return;
        } else if (brojKarata <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Odaberite broj karata!");
            alert.setContentText("Broj karata za film nije odabran.");
            alert.showAndWait();
            return;
        }
        while (brojKarata != 0) {
            Karta k = new Karta();
            k.setFilm(DaoFactory.filmDao().getByIme(imeOdabranogFilma));
            k.setUser(user);
            DaoFactory.kartaDao().add(k);
            brojKarata--;
        }
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/KupljenaKarta.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        KupljenaKartaController kkc = fxmlLoader.getController();
        kkc.imeFilma.setText("Ime filma: " + imeOdabranogFilma);
        kkc.datumFilma.setText("Datum: " + datum);
        kkc.cijenaKarte.setText("Cijena: " + ukupnaCijena + "KM");
        stage.setResizable(false);
        stage.getIcons().add(new Image("https://cdn-icons-png.flaticon.com/512/3418/3418886.png"));
        stage.setTitle("Karta kupljena!");
        stage.setScene(scene);
        stage.show();
    }

    public void nazadButtonClick(ActionEvent actionEvent) throws IOException {
        if (user.isAdmin()) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/AdminPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            AdminPageController apc = fxmlLoader.getController();
            stage.setResizable(false);
            stage.getIcons().add(new Image("https://cdn-icons-png.flaticon.com/512/3418/3418886.png"));
            stage.setTitle("Admin page");
            stage.setScene(scene);
            stage.show();
        } else {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/UserPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            UserPageController upc = fxmlLoader.getController();
            stage.setResizable(false);
            stage.getIcons().add(new Image("https://cdn-icons-png.flaticon.com/512/3418/3418886.png"));
            stage.setTitle("User page");
            stage.setScene(scene);
            stage.show();
        }
        Node n = (Node) actionEvent.getSource();
        Stage stage2 = (Stage) n.getScene().getWindow();
        stage2.close();
    }
}
