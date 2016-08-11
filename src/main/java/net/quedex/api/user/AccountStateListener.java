package net.quedex.api.user;

@FunctionalInterface
public interface AccountStateListener {

    void onAccountState(AccountState accountState);
}
