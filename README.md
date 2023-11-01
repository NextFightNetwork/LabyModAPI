<div align="center">
 <h1>LabyMod Server API</h1>
 <p>1.20.2<p>
 <p>Original: https://www.spigotmc.org/resources/92724/</p> 
</div>


# Usage

For example set a server banner (tablist) & send a player to a different minecraft server
```java
LabyModAPI labyModAPI = LabyModAPI.getInstance();

labyModAPI.sendServerBanner(player, "https://yourdomain.com/image.png");
labyModAPI.sendClientToServer(player, "Your title", "nextfight.net", true);
```
