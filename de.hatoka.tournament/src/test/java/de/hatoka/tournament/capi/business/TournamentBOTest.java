package de.hatoka.tournament.capi.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.hatoka.common.capi.business.Money;
import de.hatoka.test.DerbyEntityManagerRule;

public class TournamentBOTest
{
    private static final Date NOW = new Date();
    private static final String ACCOUNT_REF = TournamentBOTest.class.getSimpleName();
    private static final Money BUY_IN = Money.valueOf("5 EUR");
    @Rule
    public DerbyEntityManagerRule rule = new DerbyEntityManagerRule();

    @Inject
    private TournamentBusinessFactory factory;
    private TournamentBORepository tournamentBORepository;
    private TournamentBO underTest;

    @Before
    public void createTestObject()
    {
        TestBusinessInjectorProvider.get(rule.getModule()).injectMembers(this);
        tournamentBORepository = factory.getTournamentBORepository(ACCOUNT_REF);
        underTest = tournamentBORepository.createTournament("test", NOW);
    }

    @Test
    public void testAssignUnassign()
    {
        PlayerBO player1 = factory.getPlayerBORepository(ACCOUNT_REF).create("testPlayer1");
        CompetitorBO competitorBO = underTest.register(player1);
        assertTrue(underTest.getCompetitors().contains(competitorBO));
        underTest.unassign(competitorBO);
        assertFalse(underTest.getCompetitors().contains(competitorBO));
    }

    @Test
    public void testSumInPlay()
    {
        underTest.setBuyIn(BUY_IN);
        buyInPlayers(2);
        assertEquals(Money.valueOf("10 EUR"), underTest.getSumInplay());
    }

    @Test
    public void testCompetitors()
    {
        PlayerBO player1 = factory.getPlayerBORepository(ACCOUNT_REF).create("testPlayer1");
        PlayerBO player2 = factory.getPlayerBORepository(ACCOUNT_REF).create("testPlayer2");
        CompetitorBO competitorBO_1 = underTest.register(player1);
        assertTrue("player one is competitor", underTest.isCompetitor(player1));
        assertFalse("player two is not competitor", underTest.isCompetitor(player2));
        CompetitorBO competitorBO_2 = underTest.register(player2);
        underTest.buyin(competitorBO_2);
        assertFalse("player one is not active", underTest.getActiveCompetitors().contains(competitorBO_1));
        assertTrue("player two is active", underTest.getActiveCompetitors().contains(competitorBO_2));
        assertEquals("correct player assigned", player1, competitorBO_1.getPlayer());
        underTest.buyin(competitorBO_1);
        assertTrue("player one is now active", underTest.getActiveCompetitors().contains(competitorBO_1));
    }

    @Test
    public void testSimpleAttributes()
    {
        assertEquals(NOW, underTest.getStartTime());
        assertFalse("tournament.equals", underTest.equals(tournamentBORepository.createTournament("later", NOW)));
    }

    @Test
    public void testBlindLevels()
    {
        underTest.createBlindLevel(30, 100, 200, 0);
        underTest.createBlindLevel(30, 200, 400, 0);
        underTest.createPause(15);
        underTest.createBlindLevel(30, 500, 1000, 0);
        underTest.createBlindLevel(30, 500, 1000, 100);
        List<TournamentRoundBO> rounds = underTest.getTournamenRounds();
        assertEquals("five rounds added", 5, rounds.size());
        assertNull("third round is pause", rounds.get(2).getBlindLevel());
        assertEquals("second round is level with small blind", Integer.valueOf(200), rounds.get(1).getBlindLevel().getSmallBlind());
        // remove pause
        underTest.remove(rounds.get(2));
        List<TournamentRoundBO> roundsAfterDelete = underTest.getTournamenRounds();
        assertEquals("four rounds left", 4, roundsAfterDelete.size());
        for(TournamentRoundBO roundBO : roundsAfterDelete)
        {
            assertNotNull("only levels left", roundBO.getBlindLevel());
        }
    }

    @Test
    public void testPlayerLifeCycle()
    {
        PlayerBO player1 = factory.getPlayerBORepository(ACCOUNT_REF).create("testPlayer1");
        CompetitorBO competitor = underTest.register(player1);
        underTest.setReBuy(BigDecimal.TEN);
        BlindLevelBO blindLevel = underTest.createBlindLevel(30, 100, 200, 0);
        blindLevel.allowRebuy(true);
        underTest.buyin(competitor);
        underTest.start();
        Money rebuy=  underTest.getCurrentRebuy();
        assertNotNull("rebuy was defined at createBlindLevel", rebuy);
        underTest.rebuy(competitor);
        assertEquals("player one payed buyin and rebuy",rebuy.add(underTest.getBuyIn()), competitor.getInPlay());
        underTest.seatOpen(competitor);
        assertFalse("player is not longer active", competitor.isActive());
    }

    @Test
    public void testRanks()
    {
        underTest.setBuyIn(BUY_IN);
        buyInPlayers(10);
        assertEquals(Money.valueOf("50 EUR"), underTest.getSumInplay());
        underTest.createRank(1, 1, new BigDecimal("0.5")); // (50-20) * 0.5 = 15
        underTest.createRank(2, 2, new BigDecimal("0.3")); // (50-20) * 0.5 = 15
        underTest.createRank(3, 3, new BigDecimal("0.2")); // means 25% = 7.5
        underTest.createFixRank(4, 9, BigDecimal.TEN);// means 25%  = 3.75
        List<RankBO> ranks = underTest.getRanks();
        assertEquals("five rounds added", 4, ranks.size());
        assertEquals("first rank", Money.valueOf(BigDecimal.valueOf(20), getCurrency()), ranks.get(0).getAmountPerPlayer());
        assertEquals("second rank", Money.valueOf(BigDecimal.valueOf(12), getCurrency()), ranks.get(1).getAmountPerPlayer());
        assertEquals("third rank", Money.valueOf(BigDecimal.valueOf(8), getCurrency()), ranks.get(2).getAmountPerPlayer());
        assertEquals("fourth rank", Money.valueOf(BigDecimal.valueOf(1.66), getCurrency()), ranks.get(3).getAmountPerPlayer());
    }

    private Currency getCurrency()
    {
        return underTest.getBuyIn().getCurrency();
    }

    private void buyInPlayers(Integer numberOfPlayers)
    {
        for(int i=1; i<=numberOfPlayers;i++)
        {
            PlayerBO player = factory.getPlayerBORepository(ACCOUNT_REF).create("testPlayer_iterator_" + i);
            CompetitorBO competitorBO = underTest.register(player);
            underTest.buyin(competitorBO);
        }
    }

    @Test
    public void testPlaceCompetitors()
    {
        underTest.setMaximumNumberOfPlayersPerTable(10);
        for(int i = 0; i < 17; i++)
        {
            CompetitorBO competitor = createCompetitor("player testPlaceCompetitors" + i);
            underTest.buyin(competitor);
        }
        createCompetitor("inactive player testPlaceCompetitors");
        underTest.placePlayersAtTables();

        // test tables
        Collection<TableBO> tables = underTest.getTables();
        assertEquals("numberOfTables", 2, tables.size());

        // test active players
        assertEquals("maxPlayer", 9, maxPlayersOnTable(tables));
        assertEquals("sumPlayers", 17, sumPlayersOnTable(tables));

        // remove one player (now 16 - 8 on each table)
        assertTrue("no placed players at start", underTest.getPlacedCompetitors().isEmpty());
        removeFirstPlayer(tables);
        assertEquals("one placed players", 1, underTest.getPlacedCompetitors().size());
        assertEquals("one placed players", 17, underTest.getPlacedCompetitors().get(0).getPosition().intValue());

        tables = underTest.getTables();
        assertEquals("maxPlayer", 8, maxPlayersOnTable(tables));
        assertEquals("sumPlayers", 16, sumPlayersOnTable(tables));

        assertTrue("no movedPlayer", underTest.levelOutTables().isEmpty());

        // remove two player (now 14 - 6 on first and 8 on second table)
        removeFirstPlayer(tables);
        removeFirstPlayer(tables);
        tables = underTest.getTables();
        assertEquals("maxPlayer", 8, maxPlayersOnTable(tables));
        assertEquals("sumPlayers", 14, sumPlayersOnTable(tables));
        Collection<CompetitorBO> movedCompetitors = underTest.levelOutTables();
        assertEquals("one player moved", 1, movedCompetitors.size());
        tables = underTest.getTables();
        assertEquals("maxPlayer", 7, maxPlayersOnTable(tables));
        assertEquals("sumPlayers", 14, sumPlayersOnTable(tables));
    }

    @Test
    public void testRemoveLastTable()
    {
        // 9 players at 4er tables -> 3 players at 3 tables ->
        underTest.setMaximumNumberOfPlayersPerTable(4);
        for(int i=0;i<9;i++)
        {
            underTest.buyin(createCompetitor("player " + i));
        }
        underTest.placePlayersAtTables();
        // remove second of second table
        List<TableBO> tables = underTest.getTables();
        assertEquals("3 tables", 3, tables.size());
        underTest.seatOpen(tables.get(1).getCompetitors().get(1));

        // than 4 players at 2 tables and 3 moved players
        Collection<CompetitorBO> movedCompetitors = underTest.levelOutTables();
        assertEquals("3 from last table moved", 3, movedCompetitors.size());
    }

    private void removeFirstPlayer(Collection<TableBO> tables)
    {
        CompetitorBO firstCompetitor = tables.iterator().next().getCompetitors().stream().filter(c -> c.isActive()).findAny().get();
        underTest.seatOpen(firstCompetitor);
    }

    private int maxPlayersOnTable(Collection<TableBO> tables)
    {
        int maxPlayer = 0;
        for(TableBO table : tables)
        {
            final int playersOnTable = table.getCompetitors().size();
            if (maxPlayer < playersOnTable)
            {
                maxPlayer = playersOnTable;
            }
        }
        return maxPlayer;
    }

    private int sumPlayersOnTable(Collection<TableBO> tables)
    {
        int sumPlayers = 0;
        for(TableBO table : tables)
        {
            final int playersOnTable = table.getCompetitors().size();
            sumPlayers += playersOnTable;
        }
        return sumPlayers;
    }

    private CompetitorBO createCompetitor(String name)
    {
        return underTest.register(factory.getPlayerBORepository(ACCOUNT_REF).create(name));
    }
}
