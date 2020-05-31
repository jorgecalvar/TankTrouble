import gym
import numpy as np
import time
import os
import random
import math


class TankEnv(gym.Env):

    def __init__(self):

        super(TankEnv, self).__init__()

        self.action_space = gym.spaces.Discrete(5)
        self.observation_space = gym.spaces.Discrete(82944)

    def reset(self):
        time.sleep(0.1)
        return load_observation()

    def render(self, mode='human'):
        pass

    def step(self, action):
        print(action)
        i = 0
        while i<10:
            try:
                save_action(action)
                time.sleep(0.25)
                observation = load_observation()
                reward = load_reward()
                done = load_done()
                return observation, reward, done, ''
            except Exception as e:
                i += 1
        raise Exception('Continuous error!')


def load_observation():
    with open('observation.txt', 'r') as f:
        o = np.array(eval(f.read()))
    o[:,:2] = o[:,:2] // 50
    o[:,2] = o[:,2] // (math.pi/6);
    o.astype(np.int)
    return int(497664*o[0, 0] + 41472*o[0, 1] + 3456*o[0, 2] + 144*o[1, 0] + 12*o[1, 1] + o[1, 2])


def load_qtable():
    # Rows: 24*24*12*12*12*12 = 11943936
    if not os.path.exists('qtable.npz'):
        return np.zeros((11943936, 5)).astype(np.int)
    # with open('qtable.txt', 'r') as f:
    #     return eval(f.read())
    a = np.load('qtable.npz')
    return a['arr_0'].astype(np.int)


def save_qtable(qtable):
    np.savez_compressed('qtable.npz', qtable)


def load_reward():
    with open('reward.txt', 'r') as f:
        return int(f.read())


def load_done():
    with open('done.txt', 'r') as f:
        return bool(int(f.read()))


def save_action(action):
    with open('action.txt', 'w') as f:
        f.write(str(action))


# Hyperparameters
alpha = 0.1
gamma = 0.6
epsilon = 0.2

# For plotting metrics
all_epochs = []
all_penalties = []


def play_game(env):

    q_table = load_qtable()

    state = env.reset()

    done = False
    print('Entrando en bucle... State: '+str(state))
    while not done:
        if random.uniform(0, 1) < epsilon:
            action = env.action_space.sample()  # Explore action space
        else:
            q = q_table[state]
            if np.count_nonzero(q.max() == q) < 2:
                action = np.argmax(q_table[state])  # Exploit learned values
            else:
                m = q.max()
                values = []
                for i in range(len(q)):
                    if q[i] == m:
                        values.append(i)
                action = np.random.choice(values)

        next_state, reward, done, info = env.step(action)

        old_value = q_table[state, action]
        next_max = np.max(q_table[next_state])

        new_value = (1 - alpha) * old_value + alpha * (reward + gamma * next_max)
        q_table[state, action] = new_value

        state = next_state

    print(f'Saliendo del bucle: {done}')
    save_qtable(q_table)


if __name__ == '__main__':

    e = TankEnv()

    while True:

        fin = load_done()
        if not fin:
            print('Comenzando juego...')
            play_game(e)

        time.sleep(1)






