package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name, mobile);
        if(!users.contains(user)){
            users.add(user);
        }
        userPlaylistMap.put(user,userPlaylistMap.get(user));
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        if(!artists.contains(artist)){
            artists.add(artist);
        }
        artistAlbumMap.put(artist, artistAlbumMap.get(artist));
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
       Album newAlbum=new Album(title);
       Artist artist=new Artist(artistName);
       if(!albums.contains(newAlbum)){
           albums.add(newAlbum);
        }
       if(artists.contains(artist)){
           List<Album> albumList=artistAlbumMap.get(artist);
           albumList.add(newAlbum);
           artistAlbumMap.put(artist, albumList);
       }
       else{
           artists.add(artist);
           List<Album> albumList=new ArrayList<>();
           albumList.add(newAlbum);
           artistAlbumMap.put(artist, albumList);
       }
       return newAlbum;


    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album =new Album(albumName);
        Song newSong=new Song(title, length);
        if(albums.contains(album)) {
           List<Song> songList=albumSongMap.get(album);

           songList.add(newSong);
           songs.add(newSong);
           albumSongMap.put(album, songList);
           return newSong;
        }
        albums.add(album);
        if(!songs.contains(newSong)) {
            songs.add(newSong);
        }

        List<Song> songList=new ArrayList<>();
        songList.add(newSong);
        albumSongMap.put(album, songList);
        throw new Exception("Album does not exist");

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //creating playlist
        Playlist playlist=new Playlist(title);
        if(playlists.contains(playlist)){
            playlists.add(playlist);
        }

        //adding song to playListOfSong
        List<Song> newList=new ArrayList<>();
        for(Song song : songs){
            if(song.getLength()==length){
                newList.add(song);
            }
        }
        playlistSongMap.put(playlist, newList);
        //checking user is present or not present;
//
        User curruser=getCurrentUser(mobile);
        if(curruser==null){
            throw new Exception("User does not exist");
        }
        List<Playlist> playList=userPlaylistMap.get(curruser);
        if(!playList.contains(playlist)){
            playList.add(playlist);
        }
        userPlaylistMap.put(curruser, playList);

       if(playlistListenerMap.containsKey(playlist)){
           List<User> userlist=playlistListenerMap.get(playlist);
           if(!userlist.contains(curruser)){
               userlist.add(curruser);
               playlistListenerMap.put(playlist,userlist);
           }
       }
       else{
           List<User> userlist=new ArrayList<>();
           userlist.add(curruser);
           playlistListenerMap.put(playlist,userlist);

       }
        return playlist;

    }
    public User getCurrentUser(String mobile){
        for(User curr: users){
            if(curr.getMobile().equals(mobile)){
                return curr;
            }
        }
        return null;
    }


    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist=new Playlist(title);
        if(playlists.contains(playlist)){
            playlists.add(playlist);
        }
        List<Song> newList=new ArrayList<>();
        for(String songName : songTitles){
           for(Song song: songs){
               if(song.getTitle().equals(songName)){
                   newList.add(song);
               }
           }
        }
        if(playlistSongMap.containsKey(playlist)){
            List<Song> list=new ArrayList<>();
            for(Song song: newList){
                if(!list.contains(song)){
                    list.add(song);
                }
            }
            playlistSongMap.put(playlist, list);
        }else{
            playlistSongMap.put(playlist, newList);
        }


        User curruser=getCurrentUser(mobile);
        if(curruser==null){
            throw new Exception("User does not exist");
        }
        List<Playlist> playList=userPlaylistMap.get(curruser);
        if(!playList.contains(playlist)){
            playList.add(playlist);
        }
        userPlaylistMap.put(curruser, playList);

        if(playlistListenerMap.containsKey(playlist)){
            List<User> userlist=playlistListenerMap.get(playlist);
            if(!userlist.contains(curruser)){
                userlist.add(curruser);
                playlistListenerMap.put(playlist,userlist);
            }
        }
        else{
            List<User> userlist=new ArrayList<>();
            userlist.add(curruser);
            playlistListenerMap.put(playlist,userlist);

        }
        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=getCurrentUser(mobile);
        if(user==null){
           throw new Exception("User does not exist");
        }
        Playlist playlist=getcurrentPlaylist(playlistTitle);
        if(playlist==null){
            throw new Exception("Playlist does not exist");
        }
        if(creatorPlaylistMap.containsKey(user)){
           return creatorPlaylistMap.get(user);
        }
        if(playlistListenerMap.containsKey(playlist)){
            List<User> userlist=playlistListenerMap.get(playlist);
            if(userlist.contains(user)){
                return playlist;
            }
        }

        List<User> userlist=playlistListenerMap.get(playlist);
        userlist.add(user);
        playlistListenerMap.put(playlist,userlist);
        return playlist;



    }
    public Playlist getcurrentPlaylist(String playlistTitle){
        for(Playlist playlist: playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                return playlist;
            }
        }
        return null;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
             User currUser=getCurrentUser(mobile);
             Song currSong=getCurrentSong(songTitle);
             if(currUser==null){
                 throw new Exception("User does not exist");
             }
             if(currSong==null){
                 throw new Exception("Song does not exist");
             }
             if(songLikeMap.containsKey(currSong)){
                 List<User> userlist=songLikeMap.get(currSong);
                 if(!userlist.contains(currUser)){
                     userlist.add(currUser);
                     songLikeMap.put(currSong,userlist);
                 }
             }else{
                 List<User> userlist=new ArrayList<>();
                 userlist.add(currUser);
                 songLikeMap.put(currSong,userlist);
             }
             return currSong;
    }
    public Song getCurrentSong(String songTitle){
        for(Song song: songs){
            if(song.getTitle().equals(songTitle)){
                return song;
            }
        }
        return null;
    }

    public String mostPopularArtist() {
        int max=0;
        String songName="";
        for(Song song: songLikeMap.keySet()){
            List<User> userlist=songLikeMap.get(song);
            if(userlist.size()>max){
                max=userlist.size();
                songName=song.getTitle();
            }
        }
        Album album=getCurrentAlbum(songName);
        if(album==null){
            return "";
        }
        else{
            Artist artist=getCurrentArtist(album);
            if(artist==null){
                return "";
            }
            else{
                return artist.getName();
            }
        }

    }
    public Artist getCurrentArtist(Album album){
        for(Artist artist: artistAlbumMap.keySet()){
            List<Album> albumlist=artistAlbumMap.get(artist);
            if(albumlist.contains(album)){
                return artist;
            }
        }
        return null;
    }

    public Album getCurrentAlbum(String songName){
        for(Album album: albumSongMap.keySet()){
           List<Song> songlist=albumSongMap.get(album);
           for(Song song: songlist){
               if(song.getTitle().equals(songName)){
                   return album;
               }
           }
        }
        return null;
    }



    public String mostPopularSong() {
        int max=0;
        String songName="";
        for(Song song: songLikeMap.keySet()){
            List<User> userlist=songLikeMap.get(song);
            if(userlist.size()>max){
                max=userlist.size();
                songName=song.getTitle();
            }
        }
        return songName;
    }
}
