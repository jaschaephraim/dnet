# DNet: A Bayesian decision network application written in Java #

DNet is capable of representing any well-formed Bayesian decision network consisting of discrete decision nodes, discrete variable nodes, and a single utility node. Probabilistic inference can be performed exactly by enumeration or approximately by Gibbs sampling.

## Implementation ##

The application DNet is capable of representing and evaluating any well-formed Bayesian decision network consisting of discrete decision nodes, discrete variable nodes, and a single utility node.

The utility node can implement any arbitrary function on the values of its decision parents and the posterior probabilities of its variable parents. The values of variable nodes can be set as evidence, and a state can be output which represents the combination of decision node values that maximize the utility function’s output. Inference is performed, as described in _Artificial Intelligence_ (Russell & Norvig, 2010), exactly by enumeration or approximately by Gibbs sampling.

To measure the accuracy of probability calculations, Gibbs sampling error, and decision correctness, simple tests are included. The tests implement examples taken from three textbooks, as well as an implementation of the famous “Monty Hall” problem.

The package `com.jaschaephraim.dnet` contains the core classes—`DNet`, `Node`, `Discrete`, `Variable`, `Utility`, `UtilityFunction`, and `Prob`—and the sub-packages `vectors`, `exceptions`, and `tests`. The package `vectors` includes essential data structures—`NodeVector`, `Domain`, `Distribution`, `State`, and `SampleSpace`—while exceptions and tests contain peripheral classes.

The class `DNet` represents an entire decision network. It acts as a factory for all its decision, variable, and utility nodes, which are represented by instances of `Discrete extends Node`, `Variable extends Discrete`, and `Utility extends Node`, respectively. Upon construction, a utility node is passed an implementation of the interface `UtilityFunction` which must override a single abstract method: the network’s utility function.

Network arcs are established via the function `addParent(Discrete parent)` in the class `Node`. This function throws a `CycleException` if any arcs are created which would make the graph cyclic.

The domain of a discrete node—represented by an instance of the class `Domain` extends `Vector<Object>`—may consist of any set of unique objects. The class `State extends Vector<Object>` is similar, but may contain duplicates since it is used to store values from the domains of a corresponding `NodeVector` of discrete nodes.

`Distribution extends Vector<Prob>` represents a probability distribution and automatically normalizes the vector or array of `Prob` instances passed to its constructor. The `Prob` class itself extends the Java class `BigDecimal`, overriding certain methods for convenience in this application.

Each variable’s conditional probability table, as well as the network’s table of decision utilities, is represented by an instance of the class `SampleSpace`. A `SampleSpace` instance holds a reference to a `NodeVector` and maps every combination of those nodes’ values—every unique `State`—to a consecutive integer index with no wasted indices. These states are not actually held as references, but are rather mapped on the fly by the recursive functions `int domainID(int i, int j)` and `int stateID(State state)` (see figure). The `SampleSpace` maintains a vector of `Distributions` when used as a conditional probability table, and maintains a vector of `BigDecimals` when used as a decision utility table.

![SampleSpace diagram](https://github.com/jaschaephraim/dnet/SampleSpace-diagram.png)

The class `Variable` contains the methods `Distribution prior()` and `Distribution givenParents()`, on which all other probability calculations are based. The method `Distribution givenBlanket()` calculates a variable’s probability distribution given the values of the nodes in its Markov blanket, and the method `Object markovSample()` returns a random object from the variable’s domain according to that distribution.

`DNet` contains the method `Distribution prob(Variable node)` which calls either `Distribution enumeration(Variable node)` or `Distribution gibbs(Variable node)`, according to the value of the class member `boolean approx` (set to `false` by default). All of the probability functions mentioned above, in the `Variable` and `DNet` classes, also have versions which take an object as a parameter and return only the probability from the distribution which corresponds to the variable having that object as its value.

Finally, `DNet` also contains the method `State evaluate()` which iterates through all possible states of its decision nodes, records the resulting output of the utility node’s utility function, and returns the state with the highest expected utility.

## Tests ##

Each of the classes in the package `com.jaschaephraim.dnet.tests` (besides the abstract class `Test`) has a `main` method which may be executed to see a report of DNet’s performance in the console. The code in each of these tests, as well as the output, should be relatively self-explanatory and easy to follow.

The first test of the DNet application comes from an example in _Probabilistic Networks_ (Kjærulf & Madsen, 2005) and is contained in the class `com.jaschaephraim.dnet.tests.AppleJack`. It consists of a simple network with three variables, a single diagnostic decision node, and a utility node that simply returns the probability of the chosen diagnosis being correct. One variable represents the probability that an apple tree is dry, one represents the probability that an apple tree is sick, and they share a child which represents the conditional probabilities of the tree losing its leaves. In this example, the tree is observed to have lost its leaves and a diagnosis must be made as to the cause. DNet correctly diagnoses the tree as sick instead of dry with approximate expected utilities of 0.49 and 0.47, respectively.

The second test comes from _Artificial Intelligence_ (Russell & Norvig, 2010) and is contained in the class `com.jaschaephraim.dnet.tests.BurglarAlarm`. It is similar to the previous example in that the utility node represents probability of a simple diagnosis being correct, however the problem is slightly more complex. There are variables representing the probabilities of an earthquake or burglary occurring, which share a child that represents the conditional probabilities of a burglar alarm going off. That node has two children: variables representing the conditional probabilities that either of two somewhat unpredictable friends will call. In this example, both friends are observed to have called and it must be decided whether a burglary can be assumed. DNet correctly decides to assume that there is not a burglary, since the posterior probability of that occurrence—and therefore the expected utility of that assumption—is only approx. 0.28.

The third test comes from _Learning Bayesian Networks_ (Neapolitan, 2004) and is contained in the class `com.jaschaephraim.dnet.tests.BuyCar`. The problem is much more complex, involving two sequential decisions and a utility function that involves calculating monetary expected values. In this scenario, one begins with $10,000 in the bank and is considering purchasing a car for $10,000 which may or may not have a faulty transmission that can be resold for $11,000. There is a test that can be performed on the transmission for $200 prior to purchase which has some degree of error. If the transmission is indeed faulty, it can be repaired for $3,000 before the car is resold. The first decision node represents the initial choice between purchasing the test, buying the car, or walking away. The second decision—which is only relevant if the first decision is to purchase the test—is between then buying the car or then walking away. DNet correctly decides to buy the car immediately (rendering the second decision irrelevant) with an expected value of $10,400.

Finally, DNet is tested on the famous “Monty Hall” problem in the class `com.jaschaephraim.dnet.tests.MontyHall`. One node represents the probability of the car’s actual location (evenly distributed between three doors), another represents the contestant’s initial door choice (whose evidence is always set prior to evaluation), and they share a child which represents the conditional probability of each door being revealed by Monty Hall. The node representing the car’s actual location and the decision node (which represents the decision of whether to switch doors) are both parents of the utility node which, again, represents only the probability of being correct. DNet correctly decides to switch doors. More importantly, it infers the correct posterior probability distribution of the car’s location with the car being twice as likely to be behind the door that wasn’t initially chosen.

## References ##

Kjærulf, Uffe B., & Madsen, Anders L. (2005). Probabilistic networks: An introduction to Bayesian networks and influence diagrams. Aalborg, Denmark: Aalborg University Department of Computer Science.

Neapolitan, Richard E. (2004). _Learning Bayesian networks_. Prentice Hall series in artificial intelligence. Upper Saddle River, N.J: Prentice Hall/Pearson Education.

Russell, S. J., & Norvig, P. (2010). _Artificial intelligence: A modern approach_ (3rd ed.). Prentice Hall series in artificial intelligence. Upper Saddle River, N.J: Prentice Hall/Pearson Education.


