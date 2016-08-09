package net.quedex.client.user;

@FunctionalInterface
public interface AccountStateListener {

    void onAccountState(AccountState accountState);
}
