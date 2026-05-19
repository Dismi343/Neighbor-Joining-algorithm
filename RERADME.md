# Neighbor-Joining Algorithm Implementation

A Java implementation of the Neighbor-Joining algorithm for constructing phylogenetic trees from distance matrices.

## Overview

This project implements the Neighbor-Joining algorithm, a method for constructing evolutionary trees from pairwise distance data. The implementation includes:

- **Njalgo.java**: Core algorithm implementation
- **NewickTree.java**: Newick format parser and tree visualization

## Files

### Njalgo.java
Implements the Neighbor-Joining algorithm with:
- Q-matrix initialization and updates
- R-value and S-value calculations
- Distance calculations for new nodes
- Newick format tree generation

**Main Method**: Demonstrates the algorithm with 5 taxa (A, B, C, D, E) and their distance matrix.

### NewickTree.java
Parses and visualizes phylogenetic trees in Newick format:
- `readNewickFormat()`: Parses Newick strings
- `visualize()`: Displays tree structure in ASCII format
- Supports weighted branches and internal node labels

## Algorithm Steps

1. **Initialize**: Load distance matrix between sequences
2. **Calculate R-values**: Sum of distances for each taxon
3. **Calculate S-values**: Q-matrix scores to identify closest pairs
4. **Find Minimum**: Identify pair with lowest S-value
5. **Calculate Distances**: Determine branch lengths to new node
6. **Update Matrix**: Recalculate distances for remaining taxa
7. **Repeat**: Continue until only 2 nodes remain

## Running the Program

```bash
javac Njalgo.java NewickTree.java
java Njalgo
```

## Output

The program outputs:
- Q-matrix with S-values at each iteration
- Minimum S-value coordinates and distances
- Branch length calculations
- Final Newick formatted tree string
- ASCII tree visualization

## Example

Input distance matrix for taxa A, B, C, D, E produces a phylogenetic tree showing evolutionary relationships between the sequences.

## References

- Saitou, N., & Nei, M. (1987). "The neighbor-joining method: A new method for reconstructing phylogenetic trees"
- NewickTree.java source: https://stackoverflow.com/a/41418573 (CC BY-SA 3.0)
