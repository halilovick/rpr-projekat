package ba.unsa.etf.rpr;

import ba.unsa.etf.rpr.business.filmoviManager;
import ba.unsa.etf.rpr.business.karteManager;
import ba.unsa.etf.rpr.business.usersManager;
import ba.unsa.etf.rpr.domain.Film;
import ba.unsa.etf.rpr.domain.Karta;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.FilmoviException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class filmoviManagerTest {

    filmoviManager fm = new filmoviManager();
    karteManager km = new karteManager();
    usersManager um = new usersManager();

    @Test
    void deletingFilmWithRelatedTicket() throws FilmoviException {
        Film f = new Film();
        f.setIme("Test");
        fm.add(f);
        User u = new User();
        u.setIme("Ime");
        um.add(u);
        Karta k = new Karta();
        k.setUser(u);
        k.setFilm(f);
        km.add(k);
        Assertions.assertThrows(FilmoviException.class, () -> {
            fm.delete(f.getId());
        });
        km.delete(k.getId());
        fm.delete(f.getId());
        um.delete(u.getId());
    }
}
