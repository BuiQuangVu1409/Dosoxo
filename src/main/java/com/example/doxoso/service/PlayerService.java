package com.example.doxoso.service;



import com.example.doxoso.model.Player;
import com.example.doxoso.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements IPlayerService {
    @Autowired
        private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        return playerRepository.findById(id)
                .map(p -> {
                    p.setName(player.getName());
                    p.setPhone(player.getPhone());
                    p.setHoaHong(player.getHoaHong());
                    p.setHeSoCachDanh(player.getHeSoCachDanh());
                    return playerRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
    }

    @Override
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }

    @Override
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Player updateHoaHong(Long id, Double hoaHong) {
        return playerRepository.findById(id)
                .map(p -> {
                    p.setHoaHong(hoaHong);
                    return playerRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
    }

    @Override
    public Player updateHeSoCachDanh(Long id, Double heSo) {
        return playerRepository.findById(id)
                .map(p -> {
                    p.setHeSoCachDanh(heSo);
                    return playerRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
    }



    public Player addPlayer(Player player) {
        if (playerRepository.existsById(player.getId())) {
            throw new IllegalArgumentException(
                    "ID " + player.getId() + " đã tồn tại, dữ liệu: "
                            + playerRepository.findById(player.getId()).get()
            );
        }
        return playerRepository.save(player);
    }

    public Player replacePlayer(Long id, Player player) {
        // thay thế dữ liệu cũ bằng dữ liệu mới
        player.setId(id);
        return playerRepository.save(player);
    }
}


