package de.hatoka.tournament.internal.templates.app;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

import de.hatoka.common.capi.app.model.MoneyVO;
import de.hatoka.common.capi.business.Money;
import de.hatoka.tournament.internal.app.models.BlindLevelVO;
import de.hatoka.tournament.internal.app.models.CompetitorVO;
import de.hatoka.tournament.internal.app.models.PlayerVO;
import de.hatoka.tournament.internal.app.models.RankVO;
import de.hatoka.tournament.internal.app.models.TournamentVO;

public final class TournamentViewObjectHelper
{
    private TournamentViewObjectHelper()
    {
    }

    /* package */static TournamentVO getTournamentVO(String id, String name, Date date)
    {
        TournamentVO result = new TournamentVO();
        result.setId(id);
        result.setName(name);
        result.setDate(date);
        result.setBuyIn(new MoneyVO(Money.ONE_USD));
        result.setUri(URI.create("tournament/"+id+"/players.html"));
        result.setAverage(new MoneyVO(Money.ONE_USD));
        result.setSumInPlay(new MoneyVO(Money.valueOf("200", "USD")));
        result.setCompetitorsSize(20);
        result.setLargestTable(10);
        result.setInitialStack(1000);
        return result;
    }

    /* package */static PlayerVO getPlayerVO(String id, String name)
    {
        PlayerVO result = new PlayerVO();
        result.setId(id);
        result.setName(name);
        return result;
    }

    /* package */static CompetitorVO getCompetitorVO(String id, String name, String playerID)
    {
        CompetitorVO result = new CompetitorVO();
        result.setId(id);
        result.setPlayerName(name);
        result.setPlayerId(playerID);
        result.setInPlay(new MoneyVO(Money.ONE_USD));
        result.setResult(new MoneyVO(Money.NOTHING));
        result.setActive(true);
        return result;
    }

    /* package */static BlindLevelVO getBlindLevelVO(String id, int small, int big, int ante, int duration, boolean isRebuy)
    {
        BlindLevelVO result = getBlindLevelVO(small, big, ante, duration, isRebuy);
        result.setId(id);
        return result;
    }

    /* package */static BlindLevelVO getBlindLevelVO(int small, int big, int ante, int duration, boolean isRebuy)
    {
        BlindLevelVO result = new BlindLevelVO();
        result.setPause(false);
        result.setSmallBlind(small);
        result.setBigBlind(big);
        result.setAnte(ante);
        result.setDuration(duration);
        result.setRebuy(isRebuy);
        return result;
    }

    /* package */static BlindLevelVO getPauseVO(String id, int duration, boolean isRebuy)
    {
        BlindLevelVO result = new BlindLevelVO();
        result.setId(id);
        result.setPause(true);
        result.setDuration(duration);
        result.setRebuy(isRebuy);
        return result;
    }

    /* package */static RankVO getFixPriceRankVO(String id, int firstPosition, int lastPosition,
                    BigDecimal amountPerPlayer)
    {
        RankVO result = new RankVO();
        result.setId(id);
        result.setFirstPosition(firstPosition);
        result.setLastPosition(lastPosition);
        result.setAmountPerPlayer(new MoneyVO(Money.valueOf(amountPerPlayer, "USD")));
        result.setAmount(new MoneyVO(Money.valueOf(amountPerPlayer.multiply(BigDecimal.valueOf(lastPosition - firstPosition + 1)), "USD")));
        return result;
    }

    public static RankVO getPercentageRankVO(String id, int firstPosition, int lastPosition, BigDecimal percentage, BigDecimal amount)
    {
        RankVO result = new RankVO();
        result.setId(id);
        result.setFirstPosition(firstPosition);
        result.setLastPosition(lastPosition);
        result.setPercentage(percentage);
        result.setAmountPerPlayer(new MoneyVO(Money.valueOf(amount.divide(BigDecimal.valueOf(lastPosition - firstPosition + 1), 2, BigDecimal.ROUND_DOWN).stripTrailingZeros(), "USD")));
        result.setAmount(new MoneyVO(Money.valueOf(amount, "USD")));
        return result;
    }

    public static RankVO getPercentageCalcRankVO(String id, int firstPosition, int lastPosition, BigDecimal percentageCalculated,
                    BigDecimal amount)
    {
        RankVO result = getPercentageRankVO(id, firstPosition, lastPosition, percentageCalculated, amount);
        return result;
    }

}
