import Duck from './duck.js';
import Cow from './subdirectory/cow.js';

const duck = new Duck('Waddles');
const cow = new Cow('Bessie');

duck.quack();
cow.moo()