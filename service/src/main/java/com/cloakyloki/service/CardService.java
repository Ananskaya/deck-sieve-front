package com.cloakyloki.service;

import com.cloakyloki.dto.CardCreateUpdateDto;
import com.cloakyloki.dto.CardFilter;
import com.cloakyloki.dto.CardReadDto;
import com.cloakyloki.mapper.CardCreateUpdateMapper;
import com.cloakyloki.mapper.CardReadMapper;
import com.cloakyloki.repository.CardRepository;
import com.cloakyloki.repository.predicate.QPredicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.cloakyloki.entity.QCard.card;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardReadMapper cardReadMapper;
    private final CardCreateUpdateMapper cardCreateUpdateMapper;

    public Page<CardReadDto> findAll(CardFilter filter, Pageable pageable) {
        var predicate = QPredicate.builder()
                .add(filter.name(), card.name::containsIgnoreCase)
                .add(filter.manaValue(), card.manaValue::eq)
                .add(filter.manacost(), card.manacost::containsIgnoreCase)
                .add(filter.rarity(), card.rarity::containsIgnoreCase)
                .add(filter.type(), card.type::containsIgnoreCase)
                .add(filter.subtype(), card.subtype::containsIgnoreCase)
                .add(filter.supertype(), card.supertype::containsIgnoreCase)
                .add(filter.text(), card.text::containsIgnoreCase)
                .add(filter.flavorText(), card.flavorText::containsIgnoreCase)
                .add(filter.keywords(), card.keywords::containsIgnoreCase)
                .add(filter.power(), card.power::eq)
                .add(filter.artist(), card.artist::containsIgnoreCase)
                .add(filter.toughness(), card.toughness::eq)
                .add(filter.frameVersion(), card.frameVersion::eq)
                .add(filter.isBanned(), card.isBanned::eq)
                .buildAnd();
        return cardRepository.findAll(predicate, pageable)
                .map(cardReadMapper::map);
    }


    public Optional<CardReadDto> findById(Long id) {
        return cardRepository.findById(id)
                .map(cardReadMapper::map);
    }

    @Transactional
    public CardReadDto create(CardCreateUpdateDto card) {
        return Optional.of(card)
                .map(cardCreateUpdateMapper::map)
                .map(cardRepository::saveAndFlush)
                .map(cardReadMapper::map)
                .orElseThrow();
    }

    @Transactional
    public Optional<CardReadDto> update(Long id, CardCreateUpdateDto cardCreateUpdateDto) {
        return cardRepository.findById(id)
                .map(userEntity -> cardCreateUpdateMapper.map(cardCreateUpdateDto, userEntity))
                .map(cardRepository::saveAndFlush)
                .map(cardReadMapper::map);
    }

    @Transactional
    public boolean delete(Long id) {
        return cardRepository.findById(id)
                .map(card -> {
                    cardRepository.delete(card);
                    cardRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
