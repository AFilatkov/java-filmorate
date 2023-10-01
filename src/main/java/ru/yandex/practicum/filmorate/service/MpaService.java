package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getMpaById(Integer id) throws NotFound {
        return mpaDbStorage.getMpaById(id);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }
}
