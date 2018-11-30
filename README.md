# TOTOREGALO

Attraverso un'estrazione casuale, associa ad ogni giocatore il nome della persona a cui quel giocatore dovrà fare un regalo, e lo avvisa tramite email.
Ogni giocatore riceve e fa uno ed un solo regalo.

I nomi dei giocatori ed i rispettivi indirizzi email vanno salvati in un file testuale in formato csv, come in questo [esempio](./example/totoexample):

```
player1;player1@email.com
player2;player2@email.com
```

Una volta preparato il file dei partecipanti, spostarsi nella directory principale e lanciare la compilazione e l'esecuzione tramite:

```
ant run -Danno=2018 -Dpartecipanti=example/totoexample -Dinvalidpairs=example/invalidpairs
```
