package de.hatoka.tournament.modules;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.hatoka.tournament.capi.dao.BlindLevelDao;
import de.hatoka.tournament.capi.dao.CompetitorDao;
import de.hatoka.tournament.capi.dao.HistoryDao;
import de.hatoka.tournament.capi.dao.PlayerDao;
import de.hatoka.tournament.capi.dao.RankDao;
import de.hatoka.tournament.capi.dao.TournamentDao;
import de.hatoka.tournament.internal.dao.BlindLevelDaoJpa;
import de.hatoka.tournament.internal.dao.CompetitorDaoJpa;
import de.hatoka.tournament.internal.dao.HistoryDaoJpa;
import de.hatoka.tournament.internal.dao.PlayerDaoJpa;
import de.hatoka.tournament.internal.dao.RankDaoJpa;
import de.hatoka.tournament.internal.dao.TournamentDaoJpa;

public class TournamentDaoJpaModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(TournamentDao.class).to(TournamentDaoJpa.class).asEagerSingleton();
        binder.bind(PlayerDao.class).to(PlayerDaoJpa.class).asEagerSingleton();
        binder.bind(CompetitorDao.class).to(CompetitorDaoJpa.class).asEagerSingleton();
        binder.bind(HistoryDao.class).to(HistoryDaoJpa.class).asEagerSingleton();
        binder.bind(BlindLevelDao.class).to(BlindLevelDaoJpa.class).asEagerSingleton();
        binder.bind(RankDao.class).to(RankDaoJpa.class).asEagerSingleton();
    }
}
