package net.quedex.client.account;

@FunctionalInterface
public interface AccountStateListener {

    void onAccountState(AccountState accountState);
}
