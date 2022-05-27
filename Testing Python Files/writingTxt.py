# from typing import List
#
#
# def neighbouringElements(num: List[List[int]]) -> List[List[int]]:
#     # Write your code here.
#     neighbours = [(0, -1), (0, 1), (1, 0), (-1, 0)]
#     jum = []
#     height = len(num)
#     width = len(num[0])
#     for i in range(height):
#         columnList = []
#         for j in range(width):
#             sum_ = 0
#             for neighbour in neighbours:
#                 indexRow = neighbour[0] + i
#                 indexColumn = neighbour[1] + j
#                 if 0 <= indexRow < height and 0 <= indexColumn < width:
#                     sum_ += num[indexRow][indexColumn]
#             print(sum_)
#             columnList.append(sum_)
#         jum.append(columnList)
#     return jum
#
# print(neighbouringElements([[3,4], [2,3]]))
#
# print('123')

from typing import *


# def canBePalindrome(n: int, s: str) -> int:
#     # Write your code here.
#     if n % 2 != 0:
#         return 0
#     # for j, i in zip(range(n-1, int(n/2), -1), range(0, int(n/2))):
#     j = n-1
#     i = 0
#     while i != j:
#         # print(i, j)
#         if s[i] == s[j]:
#             i += 1
#             j -= 1
#             continue
#         else:
#             # print("in else")
#             new_string = s[:i+1] + s[i+1: j+1] + s[i] + s[j+1:]
#             other_String = s[:i] + s[j] + s[i: j+1] + s[j+1:]
#             print(other_String)
#             if new_string == new_string[::-1] or other_String == other_String[::-1]:
#                 return 1
#             else:
#                 return 0
#     return 0
#
#
# print(canBePalindrome(19, "trqzgtmgttgmtgzqrct"))

from typing import *


# def maximumCoins(n: int, m: int, a: List[List[int]]) -> int:
#     # Write your code here.
#     alice = [0, 0]
#     alice_vec = (0, 1)
#     bob_vec = (-1, 0)
#     bob = [n-1, 0]
#     money = 0
#     while True:
#         # print(alice, bob)
#         if alice != bob:
#             money += a[alice[0]][alice[1]] + a[bob[0]][bob[1]]
#         else:
#             money += a[alice[0]][alice[1]]
#         if bob == [0, m - 1] and alice == [n - 1, m - 1]:
#             break
#         alice[0] = alice[0] + alice_vec[0]
#         alice[1] = alice[1] + alice_vec[1]
#         bob[0] = bob[0] + bob_vec[0]
#         bob[1] = bob[1] + bob_vec[1]
#         if alice[1]+1 > int(m/2):
#             alice_vec = (1, 0)
#         if alice[0] + 1 > n-1:
#             alice_vec = (0, 1)
#         if bob[0] + 1 > int(n/2):
#             bob_vec = (0, 1)
#         if bob[1] + 1 > m-1:
#             bob_vec = (-1, 0)
#     return money
#
#
# # print(maximumCoins(3, 5,  [[2, 5, 5, 8, 2], [8, 5, 8, 3, 1], [3, 2, 1, 7, 3]]))
# stack = [1, 2, 2, 3]
# stack.insert(0, 5)
# print(stack)
# print("ads".split(" "))

# def equalStacks(h1, h2, h3):
#     # Write your code here
#     sum1 = sum(h1)
#     sum2 = sum(h2)
#     sum3 = sum(h3)
#     sums = [sum1, sum2, sum3]
#     sums.sort()
#     while sums[0] != sums[1] and sums[1] != sums[2]:
#         if sums[2] > sums[1]:
#             sums[2] -= sums[2].pop(0)
#         if sums[1] > sums[2]:
#             sums[1] -= sums[1].pop(0)
#         if sums[0] > sums[1]:
#             sums[0] -= sums[0].pop(0)
#     return sums[0]
#
# # print(sorted([15,3,6,34,1]))
# print((1, 2) + (3, 7))


vectors = [(0, 1), (1, 0), (0, -1), (-1, 0)]


class Solution:
    def depthSearch(self, word: str, alreadyVisited, indexes):
        print(alreadyVisited)
        if len(word) == 0 or (len(word) == 1 and word[0] == self.board[indexes[0]][indexes[1]]):
            return True
        if self.board[indexes[0]][indexes[1]] == word[0]:
            for vector in vectors:
                newIndexes = (vector[0] + indexes[0], vector[1] + indexes[1])
                if 0 <= newIndexes[0] < self.height and 0 <= newIndexes[1] < self.width and alreadyVisited.get(newIndexes) is None:
                    alreadyVisited[indexes] = True
                    result = self.depthSearch(word[1:], alreadyVisited, newIndexes)
                    if result:
                        return True
            return False
        else:
            return False

    def exist(self, board: List[List[str]], word: str) -> bool:
        self.board = board
        self.height = len(board)
        self.width = len(board[0])
        for i in range(0, self.height):
            for j in range(0, self.width):
                result = self.depthSearch(word, {}, (i, j))
                if result:
                    return True
        return False

    def __init__(self):
        self.board = None
        self.height = 0
        self.width = 0

# obj   = Solution()
# print(obj.exist(
# [["A","A","A","A","A","A"],["A","A","A","A","A","A"],["A","A","A","A","A","A"],["A","A","A","A","A","A"],["A","A","A","A","A","B"],["A","A","A","A","B","A"]], "AAAAAAAAAAAAABB"))

for i in range(5, 0, -1):
    print(i)
