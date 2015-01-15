package name.abuchen.portfolio;

import java.math.BigDecimal;
import java.util.Date;

import name.abuchen.portfolio.money.CurrencyConverter;
import name.abuchen.portfolio.money.CurrencyUnit;
import name.abuchen.portfolio.money.ExchangeRate;
import name.abuchen.portfolio.money.ExchangeRateTimeSeries;
import name.abuchen.portfolio.money.MonetaryException;
import name.abuchen.portfolio.money.Money;
import name.abuchen.portfolio.money.impl.ExchangeRateTimeSeriesImpl;
import name.abuchen.portfolio.money.impl.InverseExchangeRateTimeSeries;
import name.abuchen.portfolio.util.Dates;

@SuppressWarnings("nls")
public class TestCurrencyConverter implements CurrencyConverter
{
    private static ExchangeRateTimeSeriesImpl EUR_USD = null;

    static
    {
        EUR_USD = new ExchangeRateTimeSeriesImpl(null, CurrencyUnit.EUR, "USD");
        EUR_USD.addRate(new ExchangeRate(Dates.date("2014-12-31"), BigDecimal.valueOf(1.2141).setScale(10)));
        EUR_USD.addRate(new ExchangeRate(Dates.date("2015-01-02"), BigDecimal.valueOf(1.2043).setScale(10)));
        EUR_USD.addRate(new ExchangeRate(Dates.date("2015-01-14"), BigDecimal.valueOf(1.1775).setScale(10)));
        EUR_USD.addRate(new ExchangeRate(Dates.date("2015-01-31"), BigDecimal.valueOf(1.2141).setScale(10)));
    }

    private final String termCurrency;
    private final ExchangeRateTimeSeries series;

    public TestCurrencyConverter()
    {
        this(CurrencyUnit.EUR, new InverseExchangeRateTimeSeries(EUR_USD));
    }

    public TestCurrencyConverter(String currencyCode, ExchangeRateTimeSeries series)
    {
        this.termCurrency = currencyCode;
        this.series = series;
    }

    @Override
    public String getTermCurrency()
    {
        return termCurrency;
    }

    @Override
    public Money convert(Date date, Money amount)
    {
        if (termCurrency.equals(amount.getCurrencyCode()))
            return amount;

        if (amount.isZero())
            return Money.of(termCurrency, 0);

        if (!amount.getCurrencyCode().equals(series.getBaseCurrency()))
            throw new MonetaryException();

        ExchangeRate rate = getRate(date, amount.getCurrencyCode());
        BigDecimal converted = rate.getValue().multiply(BigDecimal.valueOf(amount.getAmount()));
        return Money.of(termCurrency, Math.round(converted.doubleValue()));
    }

    @Override
    public ExchangeRate getRate(Date date, String currencyCode)
    {
        if (termCurrency.equals(currencyCode))
            return new ExchangeRate(date, BigDecimal.ONE);

        if (!currencyCode.equals(series.getBaseCurrency()))
            throw new MonetaryException();

        return series.lookupRate(date).get();
    }

    @Override
    public CurrencyConverter with(String currencyCode)
    {
        if (currencyCode.equals(termCurrency))
            return this;

        if (currencyCode.equals(CurrencyUnit.EUR))
            return new TestCurrencyConverter();

        if (currencyCode.equals("USD"))
            return new TestCurrencyConverter("USD", EUR_USD);

        return null;
    }
}
