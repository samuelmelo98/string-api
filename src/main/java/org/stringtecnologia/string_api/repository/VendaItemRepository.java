package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.VendaItem;

import java.util.List;

public interface VendaItemRepository
        extends JpaRepository<VendaItem, Long> {

    List<VendaItem> findByVendaVendaId(
            Long vendaId
    );

}
