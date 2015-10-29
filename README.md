# graph
Proceedural Content Generation prototype for creating DOOM-like level maps

Uses simple graph-rewrite and iterative force relaxation to generate game-like levels which make topological
(e.g. playable) sense.

### Graph Rewrite

Retains meaningful topology by applying re-writes which by definition do not alter any constrains that were already
added.  e.g. if a corridor joins two rooms, the corridor can be rewriten into a more-complex construct (say an
obstacle of some sort) with the proviso that the corridor remains passable.  Similarly two rooms which are 
not directly joined cannot become joined as a result of a rewrite.

Example:

    START -> <expandable> -> END

Can rewrite to:

    START -> JUNCTION -> RED_DOOR -> END
                \
                 --> <obstacle> -> RED_KEY

And subsequent rewrites will not add any connections to END or RED_KEY that byepass the RED_DOOR or the OBSTACLE
respectively.

### Forcefield relaxation

Layout of the graph on the 2D plane is achieved iteratively as the graph is complexified.

A forcefield approach is used, where constrained distances are driven towards their required range by forces and a
simple Newtonian integration approach is used to iteratviely relax the layout.  This iterative approach works because:

1. the rewrite rules only add local topology, which preserves more-distance topology
2. edges can become stressed, e.g. when forced longer than desired by other constrains -- typically this is because they edge
   form part the boundary to a sub-region that wants to contain more area than there is room for
   1. when this happens the most stressed edge is split into two similar edges which permit a greater length, then the
      geometry is relaxed some more
   2. and this is repeated until no stressed edges remain

### Geometric constraints

Edges have maximum and minimum lengths.  Edges also have a width which ensures there is enough space for whatever
the edge represents to be inserted (e.g. a corridor must be at least as wide as the player.)

Nodes have a simple radius, the idea of which is to reserve enough space for some contained room (or nest of rooms) of
more complex shape.

### Detailed layout

Once a desired graph skeleton and "ball and stick" geometrical arrangement has been achieved, the plan is to expand
the geometry into a full representation of all rooms, and corridors, in the form of edges and corners.  Possibly using
more relaxation to fit these tightly together...

e.g.

    ROOM -> ROOM  (graph)
    
      N1----e1------N2  (ball and stick, N1, N2 have radii, e1 has a width)
      
        _       ______
     __| |_____|      |
    |_   x_____x      |  (full geometry, the "x"s may indicate where doors should be,
      |__|     |______|   there may also be some geometry associated with doors)
      
### State of development

1. rewrite mechanism created and working
   1. could be more sophisticated
   2. uses concept of in and out edges to indicate "forwards" and "backwards" through level, may need more than this to
      correctly accommodate side-turns and loops
   3. only a few simple rules tried so far, no doors or conception of different types of non-terminal
      (such as <obstacle> and <reward>
2. iterative rewrite/relaxation mechanism created, prototyped and under more-finished construction
   1. I have seen it working however :-)
3. many unit tests created, a fair few still to go
4. detailed geometry not even thought of

### Todo

1. finish tidying rewrite/relaxation system
2. write detailed geometry layout system
3. add primitive elements of playability such as doors/monsters/user avatar/hit-points
4. play, I may see if I can get something demoable for https://itch.io/jam/procjam

### Further ideas

1. more types of expandable node as in 1-iii
2. some sort of measure of difficulty of created level above just "size"
3. different discriptions of edge directions as in 1-ii (if required)
4. edges serving roles other than "corridor", e.g. an area can be wrapped in edges of type "fortified wall" and edges 
   of type "corridor" pass through that only at nodes of type "gate"...
5. one-way edges can be introduced, as long as the output of a rewrite always leaves some an overall connectivity
   the same as it started with
6. teleporters are easy, they are just a pair of nodes tagged as having that property, this can be leveraged in
   re-write rules if we want to add some new "remote" area
   
