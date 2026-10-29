package com.example.bangla.model;

import java.util.Set;

public record Entry(String word, Set<String> aliases) {
}