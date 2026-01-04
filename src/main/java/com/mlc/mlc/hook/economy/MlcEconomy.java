package com.mlc.mlc.hook.economy;

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
    // 小数位数,不设置了，不能搞小数
    @Override
    public int fractionalDigits() {
        return 0;
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

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(UUID.fromString(playerName), (int) (Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName)) - amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(player.getUniqueId(), (int) (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(UUID.fromString(playerName), (int) (Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName)) - amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(player.getUniqueId(), (int) (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) - amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(UUID.fromString(playerName), (int) (Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName)) + amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(player.getUniqueId(), (int) (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(UUID.fromString(playerName), (int) (Moneyfilemanager.getPlayerMoney(UUID.fromString(playerName)) + amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        try {
            Moneyfilemanager.setPlayerMoney(player.getUniqueId(), (int) (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + amount));
        } catch (IllegalArgumentException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "玩家不存在");
        }
        return null;
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
