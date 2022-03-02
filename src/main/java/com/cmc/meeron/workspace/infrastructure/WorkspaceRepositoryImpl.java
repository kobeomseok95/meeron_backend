package com.cmc.meeron.workspace.infrastructure;

import com.cmc.meeron.workspace.domain.Workspace;
import com.cmc.meeron.workspace.domain.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class WorkspaceRepositoryImpl implements WorkspaceRepository {

    private final WorkspaceJpaRepository workspaceJpaRepository;

    @Override
    public List<Workspace> findMyWorkspaces(Long userId) {
        return workspaceJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Workspace> findById(Long workspaceId) {
        return workspaceJpaRepository.findById(workspaceId);
    }
}