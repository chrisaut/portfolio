package name.abuchen.portfolio.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import name.abuchen.portfolio.model.AccountTransaction.Type;
import name.abuchen.portfolio.model.AttributeType.AmountConverter;
import name.abuchen.portfolio.model.AttributeType.PercentConverter;
import name.abuchen.portfolio.money.CurrencyUnit;
import name.abuchen.portfolio.money.Values;

@SuppressWarnings("nls")
public class AccountTest {
    private Account account;
    private AccountTransaction transaction;

    @Before
    public void setup() {
        this.account = new Account();
        account.setName("Testaccount");
        account.setCurrencyCode(CurrencyUnit.EUR);
        
        this.transaction = new AccountTransaction();
        transaction.setDateTime(LocalDate.now().atStartOfDay());
        transaction.setType(AccountTransaction.Type.DEPOSIT);
        transaction.setAmount(10000);
        transaction.setCurrencyCode(CurrencyUnit.EUR);
    }

    @Test
    public void testAddTransaction() {
        assertEquals(account.getTransactions().size(), 0);

        account.addTransaction(transaction);

        assertEquals(account.getTransactions().size(), 1);
        assertTrue(account.getTransactions().contains(transaction));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddTransactionIllegalCurrencyUnit() {
        transaction.setCurrencyCode(CurrencyUnit.USD);

        account.addTransaction(transaction);
    }
}
