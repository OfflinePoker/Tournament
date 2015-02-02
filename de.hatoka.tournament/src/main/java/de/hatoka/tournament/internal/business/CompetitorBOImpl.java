package de.hatoka.tournament.internal.business;

import de.hatoka.common.capi.business.Money;
import de.hatoka.tournament.capi.business.CompetitorBO;
import de.hatoka.tournament.capi.business.PlayerBO;
import de.hatoka.tournament.capi.business.TournamentBusinessFactory;
import de.hatoka.tournament.capi.dao.CompetitorDao;
import de.hatoka.tournament.capi.entities.CompetitorPO;

public class CompetitorBOImpl implements CompetitorBO
{
    private CompetitorPO competitorPO;
    private final CompetitorDao competitorDao;
    private final TournamentBusinessFactory factory;

    public CompetitorBOImpl(CompetitorPO competitorPO, CompetitorDao competitorDao, TournamentBusinessFactory factory)
    {
        this.competitorPO = competitorPO;
        this.competitorDao = competitorDao;
        this.factory = factory;
    }

    @Override
    public void buyin(Money money)
    {
        if (isActive())
        {
            throw new IllegalStateException("Buyin not allowed at active competitors");
        }
        competitorPO.setMoneyInPlay(getInPlay().add(money).toMoneyPO());
        competitorPO.setActive(true);
    }

    @Override
    public String getID()
    {
        return competitorPO.getId();
    }

    @Override
    public Money getInPlay()
    {
        return Money.getInstance(competitorPO.getMoneyInPlay());
    }

    @Override
    public PlayerBO getPlayerBO()
    {
        return factory.getPlayerBO(competitorPO.getPlayerPO());
    }

    @Override
    public Integer getPosition()
    {
        return competitorPO.getPositition();
    }

    @Override
    public Money getResult()
    {
        return Money.getInstance(competitorPO.getMoneyResult());
    }

    @Override
    public boolean isActive()
    {
        return competitorPO.isActive();
    }

    @Override
    public void seatOpen(Money restAmount)
    {
        if (!isActive())
        {
            throw new IllegalStateException("seatOpen not allowed at inactive competitors");
        }
        if (restAmount != null)
        {
            // pay-back rest amount
            rebuy(restAmount.negate());
        }
        competitorPO.setActive(false);
        Money moneyResult = Money.getInstance(competitorPO.getMoneyInPlay()).negate();
        competitorPO.setMoneyResult(moneyResult.toMoneyPO());
    }

    @Override
    public void rebuy(Money money)
    {
        if (!isActive())
        {
            throw new IllegalStateException("Rebuy not allowed at inactive competitors");
        }
        competitorPO.setMoneyInPlay(Money.getInstance(competitorPO.getMoneyInPlay()).add(money).toMoneyPO());
    }

    @Override
    public String remove()
    {
        String result = getID();
        competitorDao.remove(competitorPO);
        competitorPO = null;
        return result;
    }

    @Override
    public void setPosition(Integer position)
    {
        competitorPO.setPositition(position);
    }

}
