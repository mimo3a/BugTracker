package at.mci.bugtracker.service;

import at.mci.bugtracker.dao.TagDao;
import at.mci.bugtracker.model.Tag;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagDao tagDao;

    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public List<Tag> listTags() {
        return tagDao.findAll();
    }

    @Transactional
    public Tag createTag(String name, String color) {
        String trimmed = requireName(name);

        // Proaktiver Duplikat-Check für eine saubere 409-Meldung (das Frontend
        // erwartet bei 409 die Nachricht "tag existiert bereits"). Die DB-seitige
        // UNIQUE-Constraint bleibt als Backstop gegen Races bestehen.
        tagDao.findByName(trimmed).ifPresent(t -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag existiert bereits");
        });

        try {
            return tagDao.save(trimmed, color);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag existiert bereits");
        }
    }

    @Transactional
    public Tag updateTag(long id, String name, String color) {
        String trimmed = requireName(name);

        tagDao.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag nicht gefunden"));

        // Namenskollision nur, wenn ein ANDERER Tag den Namen schon trägt —
        // ein No-op-Rename auf den eigenen Namen ist erlaubt.
        Optional<Tag> sameName = tagDao.findByName(trimmed);
        if (sameName.isPresent() && sameName.get().id() != id) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag existiert bereits");
        }

        try {
            return tagDao.update(id, trimmed, color).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag nicht gefunden"));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag existiert bereits");
        }
    }

    @Transactional
    public void deleteTag(long id) {
        boolean deleted = tagDao.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag nicht gefunden");
        }
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name darf nicht leer sein");
        }
        return name.trim();
    }
}
