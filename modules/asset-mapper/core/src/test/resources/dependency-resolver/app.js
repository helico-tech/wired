import Duck from './duck.js';
import Cow from './subdirectory/cow.js';
import { libraryFunction } from '@namespace/company'

const duck = new Duck('Waddles');
const cow = new Cow('Bessie');

libraryFunction()

duck.quack();
cow.moo()