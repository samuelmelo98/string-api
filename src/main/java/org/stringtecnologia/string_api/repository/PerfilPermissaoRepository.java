package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.entities.PerfilPermissao;
import org.stringtecnologia.string_api.model.entities.PerfilPermissaoId;

import java.util.Collection;
import java.util.Set;

public interface PerfilPermissaoRepository
        extends JpaRepository<PerfilPermissao, PerfilPermissaoId> {

    @Query("""
        select distinct pp.permissao.codigo
        from PerfilPermissao pp
        where pp.perfil.codigo in :perfis
          and pp.perfil.ativo = true
          and pp.permissao.ativo = true
        """)
    Set<String> buscarCodigosPermissoesPorPerfis(
            @Param("perfis") Collection<String> perfis
    );
}
