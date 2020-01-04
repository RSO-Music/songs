package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.Songs;
import si.fri.rso.samples.imagecatalog.models.entities.SongsEntity;

public class SongsConverter {

    public static Songs toDto(SongsEntity entity) {

        Songs dto = new Songs();
        dto.setSongId(entity.getSongId());
        dto.setUploadedAt(entity.getUploadedAt());
        dto.setSongName(entity.getSongName());
        dto.setAuthorId(entity.getAuthorId());
        dto.setAlbumId(entity.getAlbumId());
        dto.setSongLength(entity.getSongLength());
        dto.setUri(entity.getUri());

        return dto;

    }

    public static SongsEntity toEntity(Songs dto) {

        SongsEntity entity = new SongsEntity();
        entity.setUploadedAt(dto.getUploadedAt());
        entity.setSongName(dto.getSongName());
        entity.setAuthorId(dto.getAuthorId());
        entity.setAlbumId(dto.getAlbumId());
        entity.setSongLength(dto.getSongLength());
        entity.setUri(dto.getUri());

        return entity;

    }

}
