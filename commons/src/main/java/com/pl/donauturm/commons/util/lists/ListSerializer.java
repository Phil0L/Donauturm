package com.pl.donauturm.commons.util.lists;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListSerializer<S extends String, L extends List<E>, E> {

    private static final String delimiter = "<dataSeparator>";

    public S serialize(L list){
        return join(list);
    }

    public L deserialize(S raw, @NonNull L empty){
        empty.addAll(split(raw));
        return empty;
    }

    @SuppressWarnings("unchecked")
    protected S join(@NonNull L list){
        return (S) list.stream().map(Object::toString).collect(Collectors.joining(delimiter));
    }

    protected List<E> split(@NonNull S raw){
        return Arrays.stream(raw.split(delimiter)).map(this::createElement).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    protected E createElement(Object e){
        return (E) e;
    }

}
