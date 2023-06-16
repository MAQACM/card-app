package com.app.cards.service.impl;


import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.cards.errorhandling.CardErrorFailure;
import com.app.cards.errorhandling.RecordNotFound;
import com.app.cards.models.payloads.*;
import com.app.cards.security.UserDetailsImpl;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.cards.models.entities.Card;
import com.app.cards.repositories.CardRepository;
import com.app.cards.service.CardsService;


import reactor.core.publisher.Mono;

@Service
public class CardsServiceImpl implements CardsService {
    public static final String CARD_NOT_FOUND = "Card not found.";
    public static final String COLOR_REGEX="^#[a-zA-Z0-9]+$";
    private final CardRepository cardRepository;
    private static final Logger logger= Logger.getLogger(CardsServiceImpl.class.getName());
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public CardsServiceImpl(CardRepository cardRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.cardRepository = cardRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }
    /*
     * Card creation
     * */

    @Override
    public Mono<Card> createCard( CardCreationBody body) {
        validateCalor(body.getColor());
        return ReactiveSecurityContextHolder.getContext().flatMap(
                userContext->{
                    UserDetailsImpl userDetails= (UserDetailsImpl) userContext.getAuthentication().getPrincipal();
                    Card card=Card.builder()
                            .color(body.getColor())
                            .name(body.getName())
                            .description(body.getDescription())
                            .createdAt(Instant.now())
                            .createdBy(userDetails.getId())
                            .status(body.getStatus()==null?CardStatus.TODO.name():body.getStatus().name()).build();
                    return this.cardRepository.save(card)
                            .onErrorResume(error->{
                                logger.info(error.getMessage());
                                return Mono.error(new Exception(error.getMessage()));
                            });
                }
        ).onErrorResume(error->Mono.error(new CardErrorFailure(error.getMessage())));

    }
    /*
     * Fetch all cards
     * */

    @Override
    public Mono<MultipleCardsResp> fetchCards(CardQueryParams queryParams) {
        return ReactiveSecurityContextHolder.getContext().flatMap(
                userContext->{
                    UserDetailsImpl userDetails= (UserDetailsImpl) userContext.getAuthentication().getPrincipal();
                    int offset= queryParams.getOffset()>0? queryParams.getOffset()-1 :0;
                    PageRequest pageRequest= PageRequest.of(offset, queryParams.getLimit());
                    pageRequest.withSort(Sort.by(queryParams.getSortBy().name().toLowerCase(Locale.ROOT)).descending());
                    Query query=createQuery(userDetails,queryParams);
                    return this.r2dbcEntityTemplate.select(query.with(pageRequest),Card.class)
                            .collectList().zipWith(this.r2dbcEntityTemplate.count(query,Card.class))
                            .flatMap(
                                    cards->{
                                        PageImpl<Card> impl=new PageImpl<Card>(cards.getT1(),pageRequest, cards.getT2());
                                        Pagination pagination=new Pagination(impl.getTotalPages(),impl.getNumber()+1,impl.getNumberOfElements(),impl.getTotalElements());
                                        return Mono.just(new MultipleCardsResp(cards.getT1(),pagination));
                                    }
                            ).onErrorResume(error->Mono.error(new CardErrorFailure(error.getMessage())));
                }
        ).onErrorResume(error->Mono.error(new CardErrorFailure(error.getMessage())));
    }
    /*
     * Fetch by card id
     * */

    @Override
    public Mono<Card> FetchById(long id) {
        return ReactiveSecurityContextHolder.getContext().flatMap(
                userContext->{
                    UserDetailsImpl userDetails= (UserDetailsImpl) userContext.getAuthentication().getPrincipal();
                    Mono<Card>cardMono=userDetails.getAuthorities().contains(new SimpleGrantedAuthority(URole.ADMIN.name()))?this.cardRepository.findById(id):
                            this.cardRepository.findByIdAndCreatedBy(id,userDetails.getId());
                    return cardMono
                            .switchIfEmpty(Mono.defer(()->Mono.error(new RecordNotFound(CARD_NOT_FOUND))))
                            .onErrorResume(error->{
                                logger.info(error.getMessage());
                                return Mono.error(new CardErrorFailure(error.getMessage()));
                            });
                }
        ).onErrorResume(error->Mono.error(new CardErrorFailure(error.getMessage())));
    }
    /*
     * Card deletion
     * */

    @Override
    public Mono<DeleteResponse> deleteCard(long id) {
        return ReactiveSecurityContextHolder.getContext().flatMap(
                userContext->{
                    UserDetailsImpl userDetails= (UserDetailsImpl) userContext.getAuthentication().getPrincipal();
                    Mono<Card>cardMono=userDetails.getAuthorities().contains(new SimpleGrantedAuthority(URole.ADMIN.name()))?this.cardRepository.findById(id):
                            this.cardRepository.findByIdAndCreatedBy(id,userDetails.getId());
                    return cardMono.flatMap(resp->{
                        cardRepository.deleteById(id).subscribe();
                        return Mono.just(new DeleteResponse(true));
                            })
                            .switchIfEmpty(Mono.defer(()->Mono.error(new RecordNotFound(CARD_NOT_FOUND))))
                            .onErrorResume(error->{
                                logger.info(error.getMessage());
                                return Mono.error(new CardErrorFailure(error.getMessage()));
                            });
                }
        ).onErrorResume(error->Mono.error(new CardErrorFailure(error.getMessage())));
    }
    /*
    * Update the card
    * */

    @Override
    public Mono<Card> updateCard(CardCreationBody body) {
        validateCalor(body.getColor());
        return ReactiveSecurityContextHolder.getContext().flatMap(
                userContext->{
                    UserDetailsImpl userDetails= (UserDetailsImpl) userContext.getAuthentication().getPrincipal();
                    Mono<Card>cardMono=userDetails.getAuthorities().contains(new SimpleGrantedAuthority(URole.ADMIN.name()))?this.cardRepository.findById(body.getId()):
                            this.cardRepository.findByIdAndCreatedBy(body.getId(), userDetails.getId());
                    return cardMono.flatMap(resp->{
                                resp.setColor(body.getColor());
                                resp.setName(getUpdate(body.getName(), resp.getName()));
                                resp.setDescription(body.getDescription());
                                resp.setStatus(getUpdate(body.getStatus().name(), resp.getStatus()));
                                return this.cardRepository.save(resp).flatMap(Mono::just);
                            })
                            .switchIfEmpty(Mono.defer(()->Mono.error(new RecordNotFound(CARD_NOT_FOUND))))
                            .onErrorResume(error->{
                                logger.info(error.getMessage());
                                return Mono.error(new CardErrorFailure(error.getMessage()));
                            });
                }
        ).onErrorResume(error->Mono.error(new CardErrorFailure(error.getMessage())));
    }
    /*
     * return a valid update
     * */
    private static String getUpdate(String update,String current){
        if(update==null)return current;
        if(update.equals(current))return current;
        return update;
    }
    /*
     * create query for fetching cards
     * */
    private static Query createQuery(UserDetailsImpl userDetails,CardQueryParams queryParams){
        boolean isAdmin =userDetails.getAuthorities().contains(new SimpleGrantedAuthority(URole.ADMIN.name()));
        Criteria criteria=isAdmin?Criteria.empty():Criteria.where((SortBy.CREATED_BY.name().toLowerCase())).is(userDetails.getId());
        if(queryParams.getColor()!=null){criteria=criteria.and(SortBy.COLOR.name().toLowerCase()).is(queryParams.getColor());}
        if(queryParams.getName()!=null){criteria=criteria.and(SortBy.NAME.name().toLowerCase()).is(queryParams.getName());}
        if(queryParams.getCreatedAt()!=null){criteria=criteria.and(SortBy.CREATED_AT.name().toLowerCase()).is(queryParams.getCreatedAt());}
        if(queryParams.getStatus()!=null){criteria=criteria.and(SortBy.STATUS.name().toLowerCase()).is(queryParams.getStatus());}
        return Query.query(criteria);
    }
    private void validateCalor(String color){
        if(color!=null){
            if(color.length()!=6 || !color.startsWith("#"))throw new CardErrorFailure("Invalid color");
            Pattern pattern= Pattern.compile(COLOR_REGEX);
            Matcher matcher = pattern.matcher(color);
            if(!matcher.matches())throw new CardErrorFailure("Invalid color");
        }

    }
}
