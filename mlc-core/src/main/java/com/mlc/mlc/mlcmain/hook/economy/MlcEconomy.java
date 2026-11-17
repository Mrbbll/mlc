package com.mlc.mlc.mlcmain.hook.economy;

import com.mlc.mlc.Mlc;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;


@SuppressWarnings("deprecation")
public class MlcEconomy implements Economy {

    public MlcEconomy(Mlc mlc) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "MlcEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }
    // 货币保留两位小数
    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.valueOf(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "🪙";
    }

    @Override
    public String currencyNameSingular() {
        return "🪙";
    }

    @Override
    public boolean hasAccount(String playerName) {
        try {
            return Moneyfilemanager.hasPlayer(UUID.fromString(playerName));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return Moneyfilemanager.hasPlayer(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return Moneyfilemanager.hasPlayer(UUID.fromString(playerName));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return Moneyfilemanager.hasPlayer(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName) {
        return Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return Moneyfilemanager.getPlayerMoney(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return Moneyfilemanager.getPlayerMoney(player.getUniqueId());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName)) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return Moneyfilemanager.getPlayerMoney(player.getUniqueId()) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName)) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return Moneyfilemanager.getPlayerMoney(player.getUniqueId()) >= amount;
    }

    private EconomyResponse transact(UUID id, double amount, boolean withdraw) {
        double balance = Moneyfilemanager.getPlayerMoney(id);
        if (!Double.isFinite(amount) || amount < 0 || java.math.BigDecimal.valueOf(amount).stripTrailingZeros().scale() > 2) {
            return new EconomyResponse  (0, balance, EconomyResponse.ResponseType.FAILURE, "金额无效，最多支持两位小数");
        }
        if (withdraw && balance < amount) {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "余额不足");
        }
        double updated = java.math.BigDecimal.valueOf(balance).add(java.math.BigDecimal.valueOf(withdraw ? -amount : amount)).doubleValue();
        if (!Double.isFinite(updated)) return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "金额过大");
        Moneyfilemanager.setPlayerMoney(id, updated);
        double actual = Moneyfilemanager.getPlayerMoney(id);
        if (Double.compare(actual, updated) != 0) return new EconomyResponse(0, actual, EconomyResponse.ResponseType.FAILURE, "保存余额失败");
        return new EconomyResponse(amount, actual, EconomyResponse.ResponseType.SUCCESS, null);
    }

    private EconomyResponse transact(String name, double amount, boolean withdraw) {
        try {
            return transact(UUID.fromString(name), amount, withdraw);
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return transact(playerName, amount, true);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return transact(playerName, amount, true);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return transact(player.getUniqueId(), amount, true);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return transact(player.getUniqueId(), amount, true);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return transact(playerName, amount, false);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return transact(playerName, amount, false);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return transact(player.getUniqueId(), amount, false);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return transact(player.getUniqueId(), amount, false);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        try {
            Moneyfilemanager.createPlayer(UUID.fromString(playerName), playerName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        try {
            Moneyfilemanager.createPlayer(player.getUniqueId(), player.getName());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        try {
            Moneyfilemanager.createPlayer(UUID.fromString(playerName), playerName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        try {
            Moneyfilemanager.createPlayer(player.getUniqueId(), player.getName());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
